package com.piehouse.woorepie.customer.repository;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.estate.entity.Estate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 매도 검증용: 특정 고객이 특정 매물 보유 중인지 확인
    Optional<Account> findByCustomer_CustomerIdAndEstate_EstateId(Long customerId, Long estateId);

    @Query("select a from Account a join fetch a.estate where a.customer.customerId = :customerId")
    List<Account> findByCustomerIdWithEstate(@Param("customerId") Long customerId);

    // 고객과 매물로 계좌 찾기
    Optional<Account> findByCustomerAndEstate(Customer customer, Estate estate);

    @Query("select a from Account a join fetch a.estate where a.customer = :customer")
    List<Account> findByCustomerWithEstate(@Param("customer") Customer customer);

    @Query("select a from Account a join fetch a.customer where a.estate = :estate")
    List<Account> findByEstateWithCustomer(@Param("estate") Estate estate);

}
