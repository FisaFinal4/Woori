package com.piehouse.woorepie.trade.repository;

import com.piehouse.woorepie.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByEstate_EstateId(Long estateId);
    List<Trade> findBySeller_CustomerId(Long sellerId);
    List<Trade> findByBuyer_CustomerId(Long buyerId);
}
