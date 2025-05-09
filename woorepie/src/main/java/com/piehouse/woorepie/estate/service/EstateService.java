package com.piehouse.woorepie.estate.service;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;

public interface EstateService {

    RedisEstatePrice getRedisEstatePrice(Long estateId);

}
