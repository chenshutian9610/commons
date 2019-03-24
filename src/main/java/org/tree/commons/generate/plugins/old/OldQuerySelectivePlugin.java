package org.tree.commons.generate.plugins.old;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/4
 *
 * <p>  新增方法
 */
public class OldQuerySelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /****************************** 修改 XML *******************************/

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        generateXmlElement(document);
        return true;
    }

    private void generateXmlElement(Document document) {
        XmlElement element = _newXmlElement("select", "querySelective", "${sql}");
        document.getRootElement().addElement(element);
    }

    private XmlElement _newXmlElement(String label, String id, String content) {
        XmlElement element = new XmlElement(label);
        element.addAttribute(new Attribute("id", id));
        element.addAttribute(new Attribute("parameterType", "java.lang.String"));
        element.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        element.addElement(new TextElement(content));
        context.getCommentGenerator().addComment(element);
        return element;
    }

    /****************************** 修改 Mapper *******************************/

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateMapperMethod(interfaze);
        return true;
    }

    private void generateMapperMethod(Interface i) {
        Method method = _newMapperMethod("java.util.List<" + _getModelName(i) + ">", "querySelective",
                new Parameter(new FullyQualifiedJavaType("String"), "sql", "@Param(\"sql\")"));
        i.addMethod(method);
    }

    private Method _newMapperMethod(String returnType, String name, Parameter... params) {
        Method method = new Method();
        method.setName(name);
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        for (Parameter param : params)
            method.getParameters().add(param);
        return method;
    }

    private String _getModelName(Interface i) {
        String className = i.getType().getShortName();
        className = className.substring(0, className.indexOf("Mapper"));
        return className;
    }
}