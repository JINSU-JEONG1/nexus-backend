package com.nexus.backend.common.utils;

public class Base62Utils {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    // 인스턴스화 방지
    private Base62Utils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Base62 Encoding
     *
     * @param input 10진수 숫자 (long)
     * @return Base62 인코딩 문자열
     */
    public static String encode(long input) {
        if (input == 0) {
            return String.valueOf(BASE62.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (input > 0) {
            int remainder = (int) (input % BASE);
            sb.append(BASE62.charAt(remainder));
            input /= BASE;
        }
        return sb.reverse().toString();
    }

    /**
     * Base62 Decoding
     *
     * @param input Base62 인코딩 문자열
     * @return 10진수 숫자 (long)
     */
    public static long decode(String input) {
        long result = 0;
        long power = 1;
        for (int i = input.length() - 1; i >= 0; i--) {
            int digit = BASE62.indexOf(input.charAt(i));
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + input.charAt(i));
            }
            result += digit * power;
            power *= BASE;
        }
        return result;
    }
}