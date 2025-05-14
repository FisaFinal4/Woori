package com.piehouse.woorepie.global.service;

import com.piehouse.woorepie.global.dto.response.S3UrlResponse;

import java.util.List;

public interface S3Service {

    S3UrlResponse generateCustomerPresignedUrl(String domain, String customerEmail);

    List<S3UrlResponse> generateAgentPresignedUrl(String domain, String agentEmail);

    List<S3UrlResponse> generateEstatePresignedUrl(String domain, String estateAddress);

}
