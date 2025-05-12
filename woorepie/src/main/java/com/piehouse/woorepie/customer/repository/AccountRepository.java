package com.piehouse.woorepie.customer.repository;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.estate.entity.Estate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 매도 검증용: 특정 고객이 특정 매물 보유 중인지 확인
    Optional<Account> findByCustomer_CustomerIdAndEstate_EstateId(Long customerId, Long estateId);

    // 매수 검증용: 고객의 전체 계좌 조회
    List<Account> findByCustomer_CustomerId(Long customerId); // Object → Account 로 수정

    // 고객과 매물로 계좌 찾기
    Optional<Account> findByCustomerAndEstate(Customer customer, Estate estate);

    // 계좌 존재 여부 확인
    boolean existsByCustomerAndEstate(Customer customer, Estate estate);
    
    // 고객으로 계좌 리스트 조회
    List<Account> findByCustomer(Customer customer);

}
