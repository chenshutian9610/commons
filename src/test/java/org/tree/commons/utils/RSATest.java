package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.util.Map;

public class RSATest {
    @Test
    public void test() {

        // RSA 密钥的长度可以为 1024 和 2048，后者被称为 RSA2
        Map<String, String> keys = RSA.createKeys(1024);

        // 公钥加密
        String ciphertext = RSA.publicEncrypt("你好，中国！", keys.get(RSA.PUBLIC_KEY));

        // 私钥解密
        String text = RSA.privateDecrypt(ciphertext, keys.get(RSA.PRIVATE_KEY));

        System.out.println(String.format("密文：%s%n明文：%s", ciphertext, text));
    }
}