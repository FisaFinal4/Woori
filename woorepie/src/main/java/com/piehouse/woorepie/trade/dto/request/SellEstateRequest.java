// SellEstateRequest.java
package com.piehouse.woorepie.trade.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellEstateRequest {
    private Long estateId;
    private Integer tradeTokenAmount;
    private Integer tokenPrice;
}