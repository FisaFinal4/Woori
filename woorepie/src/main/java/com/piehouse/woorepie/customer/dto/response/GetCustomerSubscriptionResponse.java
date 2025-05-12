package com.piehouse.woorepie.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetCustomerSubscriptionResponse {

    private Long estateId;

    private String estateName;

    private Integer subTokenAmount;

    private Integer subTokenPrice;

    private LocalDateTime subDate;

    private short subStatus;

}
