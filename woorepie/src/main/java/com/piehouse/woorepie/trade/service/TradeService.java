package com.piehouse.woorepie.trade.service;

import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;

public interface TradeService {
    void buy(BuyEstateRequest request);
    void sell(SellEstateRequest request);
    void testRedisConnection();
}


