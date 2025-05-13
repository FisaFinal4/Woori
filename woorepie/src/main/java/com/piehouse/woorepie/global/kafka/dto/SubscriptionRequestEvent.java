package com.piehouse.woorepie.global.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestEvent {
    private Long customerId;
    private Long estateId;
    private Integer amount;
    private LocalDateTime subscribeDate;
}
