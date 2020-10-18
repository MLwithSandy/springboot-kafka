package com.course.kafka.broker.stream.commodity;

import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.message.OrderPatternMessage;
import com.course.kafka.broker.message.OrderRewardMessage;
import com.course.kafka.util.CommoditySteamUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import static com.course.kafka.util.CommoditySteamUtil.*;

//@Configuration
public class CommoditySixStream {

  @Bean
  public KStream<String, OrderMessage> kafkaCommodityTrading(StreamsBuilder builder) {
    var stringSerde = Serdes.String();
    var orderSerde = new JsonSerde<>(OrderMessage.class);

    var orderPatternSerde = new JsonSerde<>(OrderPatternMessage.class);
    var orderRewardSerde = new JsonSerde<>(OrderRewardMessage.class);

    KStream<String, OrderMessage> maskedOrderStream =
        builder.stream("t.commodity.order", Consumed.with(stringSerde, orderSerde))
            .mapValues(CommoditySteamUtil::maskCreditCard);

    // 1st sink stream to pattern
    final var branchProducer = Produced.with(stringSerde, orderPatternSerde);

    new KafkaStreamBrancher<String, OrderPatternMessage>()
        .branch(
            isPlastic(), kstream -> kstream.to("t.commodity.pattern-six.plastic", branchProducer))
        .defaultBranch(kstream -> kstream.to("t.commodity.pattern-six.notplastic", branchProducer))
        .onTopOf(maskedOrderStream.mapValues(CommoditySteamUtil::mapToOrderPattern));

    // 2nd sink stream to reward
    KStream<String, OrderRewardMessage> rewardStream =
        maskedOrderStream
            .filter(isLargeQuantity())
            .filterNot(isCheap())
            .map(CommoditySteamUtil.mapToOrderRewardChangeKey()); // change key and value both

    rewardStream.to("t.commodity.reward-six", Produced.with(stringSerde, orderRewardSerde));

    // 3rd sink stream to storage
    // no transformation
    KStream<String, OrderMessage> storageStream = maskedOrderStream.selectKey(generateStorageKey());

    storageStream.to("t.commodity.storage-six", Produced.with(stringSerde, orderSerde));

    // 4th stream for fraud
    // peek instead of foreach since next step is also needed
    KStream<String, OrderMessage> fraudStream =
        maskedOrderStream
            .filter((k, v) -> v.getOrderLocation().toUpperCase().startsWith("C"))
            .peek((k, v) -> this.reportFraud(v));

    fraudStream
        .map(
            (k, v) ->
                KeyValue.pair(
                    v.getOrderLocation().toUpperCase().charAt(0) + "***",
                    v.getPrice() * v.getQuantity()))
        .peek(
            (k, v) -> {
              System.out.println("fraud location :" + k + ", fraud amount: " + v);
            })
        .to("t.commodity.fraud-six", Produced.with(stringSerde, Serdes.Integer()));

    // return any stream -> just return
    return maskedOrderStream;
  }

  private static final Logger log = LoggerFactory.getLogger(CommoditySixStream.class);

  private void reportFraud(OrderMessage v) {
    log.info("Reporting fraud {}", v);
  }
}
