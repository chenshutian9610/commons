package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class QuerySelectiveByExamplePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        _addXmlElement(document);
        return true;
    }

    private void _addXmlElement(Document document) {
        List<Element> elements = document.getRootElement().getElements();
        XmlElement element = (XmlElement) document.getRootElement().getElements().get(4);
        XmlElement target = _deal(element);
        if (target == null) {
            for (Element e : elements) {
                target = _deal(e);
                if (target != null)
                    break;
            }
        }
        document.getRootElement().addElement(target);
    }

    private XmlElement _deal(Element element) {
        try {
            XmlElement target = new XmlElement("select");
            XmlElement element0 = (XmlElement) element;
            Attribute attribute = element0.getAttributes().get(0);
            if ("id".equals(attribute.getName()) && "selectByExample".equals(attribute.getValue())) {
                _dealWithDeepCopy(target, element0);
                return target;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    private void _dealWithDeepCopy(XmlElement dest, XmlElement src) {
        TextElement element = new TextElement(" ${args} ");
        for (Element e : src.getElements())
            if (e instanceof XmlElement)
                dest.addElement("include".equals(((XmlElement) e).getName()) ? element : e);
            else
                dest.addElement(e);

        Attribute attribute = new Attribute("id", "querySelectiveByExample");
        for (Attribute a : src.getAttributes())
            dest.addAttribute("selectByExample".equals(a.getValue()) ? attribute : a);
    }

    /****************************** 添加 querySelectiveByExample 方法 *******************************/

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String model = _getModelName(interfaze);
        Method querySelectiveByExample = new Method("querySelectiveByExample");
        querySelectiveByExample.addParameter(
                new Parameter(new FullyQualifiedJavaType(String.format("%sExample", model)),
                        "example", "@Param(\"example\")"));
        querySelectiveByExample.setReturnType(new FullyQualifiedJavaType(String.format("java.util.List<%s>", model)));
        querySelectiveByExample.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addMethod(querySelectiveByExample);
        return true;
    }

    private String _getModelName(Interface i) {
        String iName = i.getType().getShortName();
        return iName.substring(0, iName.indexOf("Mapper"));
    }

    /****************************** 修改 Example, 增加 args 字段 *******************************/

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field field = new Field("args", new FullyQualifiedJavaType("java.lang.String"));
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setInitializationString("\"*\"");
        topLevelClass.addField(field);

        Method getArgs = new Method("getArgs");
        getArgs.setReturnType(new FullyQualifiedJavaType("java.lang.String"));
        getArgs.setVisibility(JavaVisibility.PUBLIC);
        getArgs.addBodyLine("return args;");
        topLevelClass.addMethod(getArgs);

        Method setArgs = new Method("setArgs");
        setArgs.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.String"), "args"));
        setArgs.setVisibility(JavaVisibility.PUBLIC);
        setArgs.addBodyLine("this.args = args;");
        topLevelClass.addMethod(setArgs);
        return true;
    }
}
