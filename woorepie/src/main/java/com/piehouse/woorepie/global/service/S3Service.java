package com.piehouse.woorepie.global.service;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.dto.response.S3UrlResponse;

public interface S3Service {

    S3UrlResponse generatePresignedUrl(SessionCustomer sessionCustomer);

}
