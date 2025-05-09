package com.piehouse.woorepie.global.service;

public interface SmsService {

    void sendSms(String toNumber, String code);

    void redisSmsCode(String phoneNumber, String code);

}
