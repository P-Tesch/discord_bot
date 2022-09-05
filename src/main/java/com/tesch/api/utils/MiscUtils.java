package com.tesch.api.utils;

import java.util.Collection;

import io.netty.util.internal.ThreadLocalRandom;

public class MiscUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Number> T listSum(Collection<? extends Number> collection) {
        Number sum = 0;
        for (Number n : collection) {
            if (n instanceof Integer) {
                sum = sum.intValue() + n.intValue();
            }

            if (n instanceof Long) {
                sum = sum.longValue() + n.longValue();
            }

            if (n instanceof Float) {
                sum = sum.floatValue() + n.floatValue();
            }

            if (n instanceof Byte) {
                sum = sum.byteValue() + n.byteValue();
            }

            if (n instanceof Double) {
                sum = sum.doubleValue() + n.doubleValue();
            }

            if (n instanceof Short) {
                sum = sum.shortValue() + n.shortValue();
            }
        }
        return (T) sum;
    }

    public static Integer randomInt(Integer min, Integer max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean isEven(Integer number) {
        return number % 2 == 0;
    }
}
