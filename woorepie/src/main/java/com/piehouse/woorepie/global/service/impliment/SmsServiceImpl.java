package com.piehouse.woorepie.global.service.impliment;

import com.piehouse.woorepie.global.dto.request.SmsAuthRequest;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.service.SmsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    private final String REDIS_SMS_AUTH_KEY_PREFIX = "sms:auth:";
    private final RedisTemplate<String, String> redisStringTemplate;
    private DefaultMessageService messageService;
    private final SecureRandom secureRandom = new SecureRandom();

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE
                .initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }


    @Override
    public void createSmsAuth(SmsAuthRequest smsAuthRequest) {
        try {
            String code = String.valueOf(secureRandom.nextInt(900_000) + 100_000);

            // 인증번호 redis에 저장
            redisSmsCode(smsAuthRequest.getPhoneNumber(), code);

            // 문자 전송
            sendSms(smsAuthRequest.getPhoneNumber(), code);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // 인증 번호 문자 전송
    @Override
    public void sendSms(String toNumber, String code) {

        String content = "[woorepie]\n 인증번호는 "+code+" 입니다.";
        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(toNumber);
            message.setText(content);

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // 인증번호 redis에 3분동안 저장
    @Override
    public void redisSmsCode(String phoneNumber, String code) {
        try {
            String key = REDIS_SMS_AUTH_KEY_PREFIX + phoneNumber;
            redisStringTemplate.opsForValue().set(key, code, Duration.ofMinutes(3));
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
