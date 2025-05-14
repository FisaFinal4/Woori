package com.piehouse.woorepie.global.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAcceptMessage {

    private Long estateId;

    private List<SubscriptionCustomer> subCustomer;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionCustomer {

        private Long customerId;

        private Integer tokenPrice;

        private Integer tradeTokenAmount;

    }

}

