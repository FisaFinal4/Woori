package com.piehouse.woorepie.trade.repository;

import com.piehouse.woorepie.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query("select t from Trade t join fetch t.estate where t.seller.customerId = :customerId")
    List<Trade> findBySellerIdWithEstate(@Param("customerId") Long customerId);

    @Query("select t from Trade t join fetch t.estate where t.buyer.customerId = :customerId")
    List<Trade> findByBuyerIdWithEstate(@Param("customerId") Long customerId);

}
