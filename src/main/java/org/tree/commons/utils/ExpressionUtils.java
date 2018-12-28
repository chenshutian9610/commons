package org.tree.commons.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * @author er_dong_chen
 * @date 2018/12/26
 */
public class ExpressionUtils {
    private static String dealExpression(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String currentLine, expression, ordinary;
        int start = 0, end;
        StringBuilder sb = new StringBuilder();
        while ((currentLine = reader.readLine()) != null) {
            end = 0;
            while ((start = currentLine.indexOf("${", start + 1)) != -1) {
                end = currentLine.indexOf("}", end + 1);
                expression = currentLine.substring(start + 2, end);
                ordinary = String.format("${%s}", expression);
                if (expression.contains(":")) {
                    currentLine = currentLine.replace(ordinary, expression.substring(expression.indexOf(":") + 1));
                }
            }
            sb.append(currentLine).append("\n");
        }
        return new String(sb);
    }

    public static void main(String[] args) throws IOException {
        Resource resource = new ClassPathResource("mybatis-generate.xml");
        dealExpression(resource.getInputStream());
//        System.out.println(dealExpression(resource.getInputStream()));
    }
}
