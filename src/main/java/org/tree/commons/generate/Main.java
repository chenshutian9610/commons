package org.tree.commons.generate;

import org.tree.commons.generate.generator.CodeGenerator;
import org.tree.commons.generate.generator.TableGenerator;

/**
 * @author er_dong_chen
 * @date 18-12-11
 * <p>
 * 示例
 */
public class Main {
    public static void main(String args[]) throws Exception {
        String properties = "generator.properties";
        TableGenerator tableGenerator = new TableGenerator(properties);
        tableGenerator.generate();
        CodeGenerator.generate(tableGenerator.getTableMap());
    }
}
