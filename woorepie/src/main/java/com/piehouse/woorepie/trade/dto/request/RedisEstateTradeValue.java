package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisEstateTradeValue {
    private Long customerId;
    private int tokenAmount;
    private int tokenPrice;
    private long timestamp;

    public Object getEstateId() {
        return customerId;
    }
}