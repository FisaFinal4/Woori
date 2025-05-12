package com.piehouse.woorepie.estate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class GetEstateSimpleResponse {
    private Long estateId;
    private String estateName;
    private String estateCity;
    private Integer tokenAmount;
    private Integer estateTokenPrice;
    private LocalDateTime estateRegistrationDate;
}

