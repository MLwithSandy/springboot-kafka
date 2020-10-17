package com.course.kafka.broker.stream.commodity;

import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.message.OrderPatternMessage;
import com.course.kafka.broker.message.OrderRewardMessage;
import com.course.kafka.util.CommoditySteamUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import static com.course.kafka.util.CommoditySteamUtil.*;

//@Configuration
public class CommodityTwoStream {

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
    // summarize order item (total = price * quantity)
    KStream<String, OrderPatternMessage>[] patternStream =
        maskedOrderStream
            .mapValues(CommoditySteamUtil::mapToOrderPattern)
            .branch(CommoditySteamUtil.isPlastic(), (k, v) -> true);

    int plasticIndex = 0;
    int notPlasticIndex = 1;

    patternStream[plasticIndex].to(
        "t.commodity.pattern-two.plastic", Produced.with(stringSerde, orderPatternSerde));
    patternStream[notPlasticIndex].to(
        "t.commodity.pattern-two.notplastic", Produced.with(stringSerde, orderPatternSerde));

    // 2nd sink stream to reward
    KStream<String, OrderRewardMessage> rewardStream =
        maskedOrderStream
            .filter(isLargeQuantity())
            .filterNot(isCheap())
            .mapValues(CommoditySteamUtil::mapToOrderReward);

    rewardStream.to("t.commodity.reward-two", Produced.with(stringSerde, orderRewardSerde));

    // 3rd sink stream to storage
    // no transformation
    KStream<String, OrderMessage> storageStream =
        maskedOrderStream.selectKey(generateStorageKey());

    storageStream.to("t.commodity.storage-two", Produced.with(stringSerde, orderSerde));

    // return any stream -> just return
    return maskedOrderStream;
  }
}
