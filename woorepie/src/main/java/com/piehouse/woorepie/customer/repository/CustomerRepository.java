package com.piehouse.woorepie.customer.repository;

import com.piehouse.woorepie.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCustomerEmail(String email);

    boolean existsByCustomerPhoneNumber(String phoneNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Customer> findByCustomerEmail(String email);

}
