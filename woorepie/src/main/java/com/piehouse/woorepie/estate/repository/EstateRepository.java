package com.piehouse.woorepie.estate.repository;

import com.piehouse.woorepie.estate.entity.Estate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
    Optional<Integer> findTokenAmountByEstateId(Long estateId);
}
