package org.tree.commons.utils;

import java.util.Random;

/**
 * @author er_dong_chen
 * @date 2018/12/20
 */
public class RandomUtils {
    public static String number(int size) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(random.nextInt(10));
        }
        return new String(sb);
    }
}
