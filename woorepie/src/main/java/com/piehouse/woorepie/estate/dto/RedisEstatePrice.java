package com.piehouse.woorepie.estate.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RedisEstatePrice {

    private Integer estatePrice;

    private Integer estateTokenPrice;

    private Integer tokenAmount;

    private BigDecimal dividendYield;

}
