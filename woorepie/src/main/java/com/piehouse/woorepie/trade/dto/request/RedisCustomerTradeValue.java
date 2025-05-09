package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisCustomerTradeValue {
    private Long estateId;
    private int tradeTokenAmount ;
    private int tokenPrice;
    private long timestamp;
}
