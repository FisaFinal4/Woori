package com.piehouse.woorepie.trade.service;

import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import org.springframework.stereotype.Service;

@Service
public interface TradeService {
    void buy(BuyEstateRequest request);
    void sell(SellEstateRequest request, Long customerId);
    void testRedisConnection();
}


