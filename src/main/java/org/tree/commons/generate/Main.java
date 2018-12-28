package org.tree.commons.generate;

import org.tree.commons.generate.generator.TableGenerator;

/**
 * @author er_dong_chen
 * @date 18-12-11
 * <p>
 * 示例
 */
class Main {
    /* 更新 tableMap 中所含的表如果只更新部分表需要手动修改 LocalMapperMap 类 */
    /* 如果更新所有表则保证不修改 model，mapper 中的类，因为修改了也会被覆盖 */
    public static void main(String args[]) throws Exception {
        String packageToScan = Main.class.getPackage().getName();
        TableGenerator generator = new TableGenerator(packageToScan);
        generator.forward();    //  正向工程
        generator.reverse();    //  逆向工程
    }
}
