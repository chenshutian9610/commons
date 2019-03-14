package org.tree.commons.utils.img;

import org.testng.annotations.Test;
import org.tree.commons.utils.AuthCodePicture;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author er_dong_chen
 * @date 2018/12/18
 */
public class AuthCodePictureTest {
    @Test
    public void testGenerate() throws Exception {
        File file = new File("D:\\Project\\template\\img\\test.jpg");
        String code = AuthCodePicture.generate(new FileOutputStream(file));
        System.out.println(code);
    }
}
