package com.piehouse.woorepie.estate.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetEstateDetailsResponse {

    private Long estateId;

    private Long agentId;

    private String agentName;

    private String estateName;

    private String estateAddress;

    private Integer tokenAmount;

    private String estateDescription;

    private String subGuideUrl;

    private String securitiesReportUrl;

    private String investmentExplanationUrl;

    private String propertyMngContractUrl;

    private String appraisalReportUrl;

    private Integer estateTokenPrice;
    
}
