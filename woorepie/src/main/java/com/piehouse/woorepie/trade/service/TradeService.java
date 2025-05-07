package com.piehouse.woorepie.trade.service;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.entity.Trade;

import java.util.List;

public interface TradeService {
    // 거래 저장
    Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice);

    // 조회 메소드
    List<Trade> getTradesByEstateId(Long estateId);
    List<Trade> getTradesBySellerId(Long sellerId);
    List<Trade> getTradesByBuyerId(Long buyerId);

    void buy(BuyEstateRequest request, Long customerId);
    void sell(SellEstateRequest request, Long customerId);
}