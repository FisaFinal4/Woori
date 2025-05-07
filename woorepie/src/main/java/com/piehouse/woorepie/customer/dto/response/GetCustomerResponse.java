package com.piehouse.woorepie.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetCustomerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    String customerName;

    String customerEmail;

    String customerPhoneNumber;

    String customerAddress;

    String accountNumber;

    Integer accountBalance;

    Integer totalAccountTokenPrice;

    LocalDateTime customerJoinDate;

}
