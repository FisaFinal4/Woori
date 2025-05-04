package com.piehouse.woorepie.customer.repository;

import com.piehouse.woorepie.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCustomerEmail(String email);

    boolean existsByCustomerPhoneNumber(String phoneNumber);

}
