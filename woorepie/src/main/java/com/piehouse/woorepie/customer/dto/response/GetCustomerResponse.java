package com.piehouse.woorepie.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetCustomerResponse {

    String customerName;

    String customerEmail;

    String customerPhoneNumber;

    String customerAddress;

    String accountNumber;

    Integer accountBalance;

    Integer totalAccountTokenPrice;

    LocalDateTime customerJoinDate;

}
