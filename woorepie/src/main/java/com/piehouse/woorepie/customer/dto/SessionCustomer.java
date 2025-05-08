package com.piehouse.woorepie.customer.dto;

import com.piehouse.woorepie.customer.entity.Customer;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
public class SessionCustomer implements UserDetails {

    private final Long customerId;

    private final String customerName;

    private final String customerEmail;

    private final Collection<? extends GrantedAuthority> authorities;

    public static SessionCustomer fromCustomer(Customer customer) {
        return new SessionCustomer(customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getCustomerEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
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
