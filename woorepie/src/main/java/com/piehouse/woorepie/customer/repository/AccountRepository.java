package com.piehouse.woorepie.customer.repository;

import com.piehouse.woorepie.customer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 매도 검증용: 특정 고객이 특정 매물 보유 중인지 확인
    Optional<Account> findByCustomer_CustomerIdAndEstate_EstateId(Long customerId, Long estateId);

    // 매수 검증용: 고객의 전체 계좌 조회
    Optional<Account> findByCustomer_CustomerId(Long customerId); // ✅ Object → Account 로 수정


}
