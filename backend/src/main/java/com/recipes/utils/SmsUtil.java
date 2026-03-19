package com.recipes.utils;

import java.util.Random;

public class SmsUtil {
    private static final Random random = new Random();
    private static final int CODE_LENGTH = 6;

    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 这里可以添加发送短信的实际实现
    // 由于是示例，这里只打印验证码
    public static void sendSms(String phone, String code) {
        System.out.println("向手机号 " + phone + " 发送验证码: " + code);
        // 实际项目中，这里应该调用短信服务提供商的API
    }
}
