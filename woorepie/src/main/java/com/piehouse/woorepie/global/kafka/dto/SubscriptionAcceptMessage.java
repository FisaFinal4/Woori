package com.piehouse.woorepie.global.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAcceptMessage {
    private Long estateId;
    private List<SubscriptionCustomer> customer;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionCustomer {
        private Long customerId;
        private Integer tokenPrice;
        private Integer tradeTokenAmount;
    }
}

