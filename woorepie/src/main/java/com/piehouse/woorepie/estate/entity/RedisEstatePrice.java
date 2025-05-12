package com.piehouse.woorepie.estate.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisEstatePrice {

    private Integer estatePrice;

    private Integer estateTokenPrice;

    private Integer tokenAmount;

}
