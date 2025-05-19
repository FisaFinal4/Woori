package com.piehouse.woorepie.estate.repository;

import com.piehouse.woorepie.estate.entity.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    // 최신 배당 수익률 1건 조회
    Optional<Dividend> findTopByEstate_EstateIdOrderByDividendDateDesc(Long estateId);

}
