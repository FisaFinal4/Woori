package com.piehouse.woorepie.global.kafka.dto;

import com.piehouse.woorepie.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class CustomerCreatedEvent {

    private final Long customerId;

    private final String customerKyc;

    private final String customerIdentificationUrl;

    public static CustomerCreatedEvent fromCustomer(Customer customer) {
        return CustomerCreatedEvent.builder()
                .customerId(customer.getCustomerId())
                .customerKyc(customer.getCustomerKyc())
                .customerIdentificationUrl(customer.getCustomerIdentificationUrl())
                .build();
    }

}