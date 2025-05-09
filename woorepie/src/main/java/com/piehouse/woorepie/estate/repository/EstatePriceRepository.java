package com.piehouse.woorepie.estate.repository;

import com.piehouse.woorepie.estate.entity.EstatePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstatePriceRepository extends JpaRepository<EstatePrice, Long> {

    // 특정 매물의 최신 가격 1건 조회
    Optional<EstatePrice> findTopByEstate_EstateIdOrderByEstatePriceDateDesc(Long estateId);

    // 특정 매물의 모든 시세 내역 조회 (최신순)
    List<EstatePrice> findAllByEstate_EstateIdOrderByEstatePriceDateDesc(Long estateId);
}
