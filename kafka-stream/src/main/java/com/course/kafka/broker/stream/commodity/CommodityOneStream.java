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

//@Configuration
public class CommodityOneStream {

    @Bean
    public KStream<String, OrderMessage> kafkaCommodityTrading(StreamsBuilder builder){
        var stringSerde = Serdes.String();
        var orderSerde = new JsonSerde<>(OrderMessage.class);

        var orderPatternSerde = new JsonSerde<>(OrderPatternMessage.class);
        var orderRewardSerde = new JsonSerde<>(OrderRewardMessage.class);

        KStream<String, OrderMessage> maskedOrderStream = builder.stream("t.commodity.order",
                Consumed.with(stringSerde, orderSerde)).mapValues(CommoditySteamUtil::maskCreditCard);

        // 1st sink stream to pattern
        //summarize order item (total = price * quantity)
        KStream<String, OrderPatternMessage> patternStream = maskedOrderStream
                .mapValues(CommoditySteamUtil::mapToOrderPattern);
        patternStream.to("t.commodity.pattern-one", Produced.with(stringSerde, orderPatternSerde));

        //2nd sink stream to reward
        KStream<String, OrderRewardMessage> rewardStream = maskedOrderStream
                .filter(CommoditySteamUtil.isLargeQuantity()).mapValues(CommoditySteamUtil::mapToOrderReward);

        rewardStream.to("t.commodity.reward-one", Produced.with(stringSerde, orderRewardSerde));

        //3rd sink stream to storage
        // no transformation
        maskedOrderStream.to("t.commodity.storage-one", Produced.with(stringSerde, orderSerde));

        //return any stream -> just return
        return maskedOrderStream;
    }
}
