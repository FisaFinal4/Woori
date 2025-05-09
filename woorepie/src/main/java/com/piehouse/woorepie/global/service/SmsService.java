package com.piehouse.woorepie.global.service;

import com.piehouse.woorepie.global.dto.request.SmsAuthRequest;

public interface SmsService {

    void createSmsAuth(SmsAuthRequest smsAuthRequest);

    void sendSms(String toNumber, String code);

    void redisSmsCode(String phoneNumber, String code);

}
