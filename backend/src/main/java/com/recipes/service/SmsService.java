package com.recipes.service;

import com.recipes.utils.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 * 开发环境使用 mock 模式：固定验证码 123456，无需真实短信
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    private static final String MOCK_CODE = "123456";
    private static final long CODE_EXPIRATION_MINUTES = 5;
    private static final String SMS_CODE_PREFIX = "sms:code:";

    // 开发环境用内存 Map 存储验证码，替代 Redis
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${app.sms.mock:true}")
    private boolean mockMode;

    public void sendSmsCode(String phone) {
        if (!PhoneValidator.validate(phone)) {
            throw new RuntimeException("手机号格式错误");
        }

        String code;
        if (mockMode) {
            // mock 模式：固定验证码 123456
            code = MOCK_CODE;
            log.info("[SMS MOCK] 手机号: {}，验证码: {}", phone, code);
        } else {
            code = generateCode();
            // TODO: 接入真实短信服务商（阿里云/腾讯云）
            log.info("[SMS] 发送验证码到: {}，验证码: {}", phone, code);
        }

        String key = SMS_CODE_PREFIX + phone;
        codeStore.put(key, code);

        // 5 分钟后自动过期
        scheduler.schedule(() -> codeStore.remove(key), CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    public boolean verifySmsCode(String phone, String code) {
        if (!PhoneValidator.validate(phone)) {
            throw new RuntimeException("手机号格式错误");
        }

        if (code == null || code.trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }

        String key = SMS_CODE_PREFIX + phone;
        String storedCode = codeStore.get(key);

        if (storedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }

        if (!storedCode.equals(code.trim())) {
            throw new RuntimeException("验证码错误");
        }

        // 验证成功后删除，防止重复使用
        codeStore.remove(key);
        return true;
    }

    private String generateCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
