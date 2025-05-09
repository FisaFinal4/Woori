package com.piehouse.woorepie.estate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetEstatePriceResponse {
    private Integer estatePrice;
    private LocalDateTime estatePriceDate;
}
