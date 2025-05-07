// BuyEstateRequest.java
package com.piehouse.woorepie.trade.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyEstateRequest {
    private Long estateId;           // 어떤 매물인지
    private Integer tradeTokenAmount; // 매수할 토큰 수량
    private Integer tokenPrice;       // 토큰 당 가격
    private Long customerId;          // 사용자 ID
}
