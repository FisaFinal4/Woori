package com.piehouse.woorepie.customer.dto;

import com.piehouse.woorepie.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class SessionCustomer implements UserDetails {

    private final Long customerId;

    private final String customerName;

    private final String customerEmail;

    private final Collection<? extends GrantedAuthority> authorities;

    public static SessionCustomer fromCustomer(Customer customer) {
        return SessionCustomer.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerEmail(customer.getCustomerEmail())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

}
