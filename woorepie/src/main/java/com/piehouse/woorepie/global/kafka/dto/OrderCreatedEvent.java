package com.piehouse.woorepie.global.kafka.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderCreatedEvent {
    private Long estateId;
    private Long customerId;
    private Integer tokenPrice;
    private Integer tradeTokenAmount;
}
