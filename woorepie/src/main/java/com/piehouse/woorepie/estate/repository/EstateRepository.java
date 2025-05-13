package com.piehouse.woorepie.estate.repository;

import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.SubState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
    
    Optional<Integer> findTokenAmountByEstateId(Long estateId);
    
    List<Estate> findBySubState(SubState subState);

    List<Estate> findBySubStateIn(List<SubState> subStates);

}
