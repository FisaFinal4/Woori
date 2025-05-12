package com.piehouse.woorepie.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetCustomerAccountResponse {

    private Long estateId;

    private String estateName;

    private Integer accountTokenAmount;

    private Integer accountTokenPrice;

}
