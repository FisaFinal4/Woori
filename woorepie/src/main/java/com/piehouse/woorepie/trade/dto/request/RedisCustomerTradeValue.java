package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisCustomerTradeValue {
    private Long estateId;
    private int tradeTokenAmount ;
    private int tokenPrice;
    private long timestamp;
}
