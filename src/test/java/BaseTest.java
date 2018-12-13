import org.testng.annotations.Test;
import org.tree.commons.utils.PropertiesUtils;
import org.tree.commons.generate.generator.CodeGenerator;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class BaseTest {
    @Test
    public void test() throws Exception {
        String path = "conf.properties";
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(path));
        System.out.println(properties);
        properties = PropertiesUtils.getProperties("conf.properties");
        System.out.println(properties);
    }

    @Test
    public void test2() throws Exception {
        URL url = CodeGenerator.class.getClassLoader().getResource("mybatis-generate.xml");
        System.out.println(url.getPath());
        File file = new File(url.getPath());
        System.out.println(file.getCanonicalPath());
    }
}
