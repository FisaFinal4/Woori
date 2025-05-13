package com.piehouse.woorepie.estate.repository;

import com.piehouse.woorepie.estate.entity.DividendYield;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DividendYieldRepository extends JpaRepository<DividendYield, Long> {

    // 최신 배당 수익률 1건 조회
    Optional<DividendYield> findTopByEstate_EstateIdOrderByDividendYieldDateDesc(Long estateId);
}
