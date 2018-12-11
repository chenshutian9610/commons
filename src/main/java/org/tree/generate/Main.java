package org.tree.generate;

import org.tree.common.utils.PropertiesUtils;
import org.tree.generate.generator.CodeGenerator;
import org.tree.generate.generator.TableGenerator;

import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 18-12-11
 * <p>
 * 示例
 */
public class Main {
    public static void main(String args[]) throws Exception {
        Properties properties = PropertiesUtils.getProperties("conf.properties");
        String packageToScan = properties.getProperty("packageToScan");

        /* 正向工程，第二个参数表示是否执行数据库脚本 */
        TableGenerator.generate(packageToScan);
        /* 逆向工程，依赖 TableGenerator */
        CodeGenerator.generate("mybatis-generate.xml", TableGenerator.getTableMap(packageToScan));

    }
}
