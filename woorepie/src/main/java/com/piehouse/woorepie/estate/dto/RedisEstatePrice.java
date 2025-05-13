package com.piehouse.woorepie.estate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisEstatePrice {

    private Integer estatePrice;

    private Integer estateTokenPrice;

    private Integer tokenAmount;

    private Integer dividendYield;

}
