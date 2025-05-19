package com.piehouse.woorepie.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellEstateRequest {

    private Long estateId;

    private Integer tradeTokenAmount;

    private Integer tokenPrice;

}