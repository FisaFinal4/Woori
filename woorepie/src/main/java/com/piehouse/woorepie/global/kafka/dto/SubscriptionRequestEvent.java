package com.piehouse.woorepie.global.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestEvent {

    private Long customerId;

    private Long estateId;

    private Integer tokenPrice;

    private Integer amount;

    private LocalDateTime subscribeDate;

}
