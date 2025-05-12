package com.piehouse.woorepie.estate.service;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;

import java.util.List;

public interface EstateService {

    RedisEstatePrice getRedisEstatePrice(Long estateId);

    // 청약 완료된 매물 리스트 조회 (거래 가능 매물)
    List<GetEstateSimpleResponse> getTradableEstates();
}
