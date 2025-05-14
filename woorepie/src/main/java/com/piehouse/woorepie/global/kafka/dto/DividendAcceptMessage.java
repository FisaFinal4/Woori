package com.piehouse.woorepie.global.kafka.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class DividendAcceptMessage {

    private Long estateId;

    private Integer dividend;

}