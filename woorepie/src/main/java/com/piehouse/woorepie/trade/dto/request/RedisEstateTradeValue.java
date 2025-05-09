package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisEstateTradeValue {
    private Long customerId;
    private int tradeTokenAmount;
    private int tokenPrice;
    private long timestamp;
}