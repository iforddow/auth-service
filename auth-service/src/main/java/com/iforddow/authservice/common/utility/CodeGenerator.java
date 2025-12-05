package com.iforddow.authservice.common.utility;

public class CodeGenerator {

    public static String generateRandomCode() {
        int length = 6;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10);
            code.append(digit);
        }
        return code.toString();
    }

}
