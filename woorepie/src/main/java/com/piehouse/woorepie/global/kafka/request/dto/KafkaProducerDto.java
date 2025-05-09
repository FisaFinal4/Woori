// SellOrderKafkaMessage.java
package com.piehouse.woorepie.global.kafka.request.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KafkaProducerDto {
    private Long estateId;
    private Long customerId;
    private Integer tokenPrice;
    private Integer tradeTokenAmount;
}
