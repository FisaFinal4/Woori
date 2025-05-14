package com.piehouse.woorepie.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetSubscriptionSimpleResponse {

    private Long estateId;

    private String estateName;

    private Long agentId;

    private String agentName;

    private LocalDateTime subStartDate;

    private LocalDateTime subEndDate;

    private String estateState;

    private String estateCity;

    private String estateImageUrl;

    private Integer tokenAmount;

    private Integer estatePrice;

}
