package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisEstateTradeValue {

    private Long customerId;

    private int tradeTokenAmount;

    private int tokenPrice;

    private long timestamp;

}