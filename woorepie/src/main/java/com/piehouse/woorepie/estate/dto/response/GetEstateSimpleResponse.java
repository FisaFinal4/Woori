package com.piehouse.woorepie.estate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetEstateSimpleResponse {
    private Long estateId;
    private String estateName;
    private String estateCity;
    private Integer tokenAmount;
    private LocalDateTime estateRegistrationDate;
}
