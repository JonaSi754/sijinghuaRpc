package org.sijinghua.rpc.common.utils;

import java.util.stream.IntStream;

public class SerializationUtils {
    private static final String PADDING_STRING = "0";

    /**
     * 约定序列化类型最大长度为16（体现在constants中）
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNT = 16;

    public static String paddingString(String str) {
        str = transNullToEmpty(str);
        if (str.length() >= MAX_SERIALIZATION_TYPE_COUNT) return str;
        int paddingCount = MAX_SERIALIZATION_TYPE_COUNT - str.length();
        StringBuilder paddingString = new StringBuilder(str);
        IntStream.range(0, paddingCount).forEach(i -> {
            paddingString.append(PADDING_STRING);
        });
        return paddingString.toString();
    }

    public static String subString(String str) {
        str = transNullToEmpty(str);
        return str.replace(PADDING_STRING, "");
    }

    private static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
