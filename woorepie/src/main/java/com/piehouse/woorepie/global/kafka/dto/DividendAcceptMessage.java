package com.piehouse.woorepie.global.kafka.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DividendAcceptMessage {
    private Long estateId;
    private BigDecimal dividendYield;
}