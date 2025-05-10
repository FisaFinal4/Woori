package com.piehouse.woorepie.subscription.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterEstateRequest {
    private String estateName;
    private String estateState;
    private String estateCity;
    private Integer estatePrice;
    private String estateAddress;
    private String estateLatitude;
    private String estateLongitude;
    private Integer tokenAmount;
    private String estateDescription;
    private String estateImageUrl;
    private String subGuideUrl;
    private String securitiesReportUrl;
    private String investmentExplanationUrl;
    private String propertyMngContractUrl;
    private String appraisalReportUrl;
}
