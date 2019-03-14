package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.util.Map;

public class RSATest {
    @Test
    public void test() {
        Map<String, String> keys = RSA.createKeys(1024);
        String ciphertext = RSA.publicEncrypt("你好，中国！", keys.get(RSA.PUBLIC_KEY));
        String text = RSA.privateDecrypt(ciphertext, keys.get(RSA.PRIVATE_KEY));
        System.out.println(String.format("密文：%s%n明文：%s", ciphertext, text));
    }
}