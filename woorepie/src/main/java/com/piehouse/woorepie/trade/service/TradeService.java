package com.piehouse.woorepie.trade.service;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.CreateSubscriptionTradeRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.entity.Trade;

public interface TradeService {
    // 거래 저장
    Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice);

    void buy(BuyEstateRequest request, Long customerId);

    void sell(SellEstateRequest request, Long customerId);

    int createSubscription(CreateSubscriptionTradeRequest request, Long customerId);

}