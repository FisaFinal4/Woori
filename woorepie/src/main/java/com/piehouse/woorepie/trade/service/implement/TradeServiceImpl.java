package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.estate.entity.entity.Estate;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.trade.entity.Trade;
import com.piehouse.woorepie.trade.repository.TradeRepository;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final EstateRepository estateRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice) {
        Trade trade = Trade.builder()
                .estate(estate)
                .seller(seller)
                .buyer(buyer)
                .tradeTokenAmount(tradeTokenAmount)
                .tokenPrice(tokenPrice)
                .tradeDate(LocalDateTime.now())
                .build();

        return tradeRepository.save(trade);
    }

    @Override
    public List<Trade> getTradesByEstateId(Long estateId) {
        return tradeRepository.findByEstate_EstateId(estateId);
    }

    @Override
    public List<Trade> getTradesBySellerId(Long sellerId) {
        return tradeRepository.findBySeller_CustomerId(sellerId);
    }

    @Override
    public List<Trade> getTradesByBuyerId(Long buyerId) {
        return tradeRepository.findByBuyer_CustomerId(buyerId);
    }
}
