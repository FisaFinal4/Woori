package com.piehouse.woorepie.estate.service;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;

import java.util.List;

public interface EstateService {

    RedisEstatePrice getRedisEstatePrice(Long estateId);

    // 전체 매물 리스트 조회 (시세 포함)
    List<GetEstateSimpleResponse> getAllEstates();



    }


