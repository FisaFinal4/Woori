package com.piehouse.woorepie.subscription.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetSubscriptionDetailsResponse {

    private Long estateId;

    private String estateName;

    private Long agentId;

    private String agentName;

    private LocalDateTime subStartDate;

    private LocalDateTime subEndDate;

    private String estateAddress;

    private String estateImageUrl;

    private Integer estatePrice;

    private Integer tokenAmount;

    private Integer subTokenAmount;

    private String investmentExplanationUrl;

    private String propertyMngContractUrl;

    private String appraisalReportUrl;

}
