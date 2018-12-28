import java.io.*;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class BaseTest {
    private static Reader dealExpression(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String currentLine, expression;
        int start = 0, end = 0;
        while ((currentLine = reader.readLine()) != null) {
            while ((start = currentLine.indexOf("${", start)) != -1) {
                end = currentLine.indexOf("}", end);
                expression = currentLine.substring(start + 1, end);
                if (expression.contains(":")) {
                    currentLine.replace(String.format("${%s}", expression), expression.substring(expression.indexOf(":") + 1, end));
                }
            }
        }
        return reader;
    }
}
