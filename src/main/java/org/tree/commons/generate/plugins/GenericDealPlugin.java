package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.tree.commons.support.mapper.BaseMapper;
import org.tree.commons.support.mapper.Example;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/13
 *
 * <p>  可配置属性有 mapperRootInterface, 默认为 BaseMapper
 */
public class GenericDealPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /****************************** Example 泛型实现 *******************************/

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateExampleRootInterface(topLevelClass);
        return true;
    }

    /* 令所有 Example 实现 Example */
    private void generateExampleRootInterface(TopLevelClass topLevelClass) {
        String extend = Example.class.getName();
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(_getGenericString(topLevelClass, extend)));
        topLevelClass.addImportedType(extend);
    }

    /* 获取泛型格式的类名 */
    private String _getGenericString(TopLevelClass topLevelClass, String className) {
        String example = topLevelClass.getType().getShortName();
        className = className + "<" + example.substring(0, example.indexOf("Example")) + ">";
        return className;
    }

    /****************************** Mapper 泛型实现 *******************************/

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateMapperRootInterface(interfaze);
        return true;
    }

    /* 令所有 Mapper 继承 BaseMapper */
    private void generateMapperRootInterface(Interface i) {
        String rootInterface = properties.getProperty("mapperRootInterface");
        if (rootInterface == null)
            rootInterface = BaseMapper.class.getName();
        i.addImportedType(new FullyQualifiedJavaType(rootInterface));
        i.addSuperInterface(new FullyQualifiedJavaType(_getGenericString(i, rootInterface.substring(rootInterface.lastIndexOf(".") + 1))));
    }

    /* 获取泛型格式的类名 */
    private String _getGenericString(Interface i, String className) {
        String mapper = i.getType().getShortName();
        className = className + "<" + mapper.substring(0, mapper.indexOf("Mapper")) + ">";
        return className;
    }
}
