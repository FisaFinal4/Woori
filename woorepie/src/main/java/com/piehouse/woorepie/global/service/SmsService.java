package com.piehouse.woorepie.global.service;

import com.piehouse.woorepie.global.dto.request.SmsCodeRequest;
import com.piehouse.woorepie.global.dto.request.SmsVerifyRequest;

public interface SmsService {

    void createSmsAuth(SmsCodeRequest smsCodeRequest);

    void sendSms(String toNumber, String code);

    void redisSmsCode(String phoneNumber, String code);

    Boolean isSmsCodeValid(SmsVerifyRequest smsVerifyRequest);
}
