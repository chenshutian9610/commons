package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.TableConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mybatis.generator.api.dom.OutputUtilities.newLine;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class QuerySelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /************************** 向 XML 文件添加 id = querySelective 的 <select/> ***************************/

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        /* <select id="querySelective" parameterType="map" resultMap="BaseResultMap" /> */
        XmlElement element = new XmlElement("select");
        element.addAttribute(new Attribute("id", "querySelective"));
        element.addAttribute(new Attribute("parameterType", "map"));
        element.addAttribute(new Attribute("resultMap", "BaseResultMap"));

        element.addElement(new TextElement("select"));

        /* <if test="example.distinct">distinct</if> */
        XmlElement _if = new XmlElement("if");
        _if.addAttribute(new Attribute("test", "example.distinct"));
        _if.addElement(new TextElement("distinct"));
        element.addElement(_if);

        String tableName = introspectedTable.getTableConfiguration().getTableName();
        element.addElement(new TextElement(String.format("${args} from %s", tableName)));

        /* <if test="example != null"><include refid="Update_By_Example_Where_Clause" /></if> */
        XmlElement _include = new XmlElement("include");
        _include.addAttribute(new Attribute("refid", "Update_By_Example_Where_Clause"));
        XmlElement _if2 = new XmlElement("if");
        _if2.addAttribute(new Attribute("test", "example != null"));
        _if2.addElement(_include);
        element.addElement(_if2);

        /* <if test="example.orderByClause != null">order by ${example.orderByClause}</if> */
        XmlElement _if3 = new XmlElement("if");
        _if3.addAttribute(new Attribute("test", "example.orderByClause != null"));
        _if3.addElement(new TextElement("order by ${example.orderByClause}"));
        element.addElement(_if3);

        document.getRootElement().addElement(element);
        return true;
    }

    /****************************** 向 Mapper 接口添加 querySelective 方法 *******************************/

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String model = introspectedTable.getTableConfiguration().getDomainObjectName();
        Method querySelective = new Method("querySelective");
        querySelective.addParameter(
                new Parameter(new FullyQualifiedJavaType(String.format("%sArgs", model)),
                        "args", "@Param(\"args\")"));
        querySelective.addParameter(
                new Parameter(new FullyQualifiedJavaType(String.format("%sExample", model)),
                        "example", "@Param(\"example\")"));
        querySelective.setReturnType(new FullyQualifiedJavaType(String.format("java.util.List<%s>", model)));
        querySelective.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addMethod(querySelective);

        String importArgs = context.getJavaModelGeneratorConfiguration().getTargetPackage() + String.format(".%sArgs", model);
        interfaze.addImportedType(new FullyQualifiedJavaType(importArgs));
        return true;
    }

    /****************************** 生成 Args 和 Enum 文件 *******************************/

    private Map<TableConfiguration, List<IntrospectedColumn>> definitions = new HashMap<>();

    @Override // 扫描所有 model，初始化 definitions
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        definitions.put(introspectedTable.getTableConfiguration(), introspectedTable.getAllColumns());
        return true;
    }

    @Override // 生成 Args 和 Enum 文件
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(definitions.size() * 2);
        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();

        GeneratedJavaFile javaFile;
        for (Map.Entry<TableConfiguration, List<IntrospectedColumn>> definition : definitions.entrySet()) {
            String modelName = definition.getKey().getDomainObjectName();
            String tableName = definition.getKey().getTableName();

            /****************************** enum *******************************/

            Enum tableEnum = new Enum(String.format("%s.%sEnum", targetPackage, modelName));
            for (IntrospectedColumn introspectedColumn : definition.getValue()) {
                tableEnum.addConstant(introspectedColumn.getActualColumnName());
                tableEnum.setTableName(tableName);
            }
            javaFile = new GeneratedJavaFile(tableEnum, targetProject, new DefaultJavaFormatter());
            javaFiles.add(javaFile);

            /****************************** args *******************************/

            String ArgsType = String.format("%s.%sArgs", targetPackage, modelName);
            TopLevelClass tableArgs = new TopLevelClass(ArgsType);
            tableArgs.addImportedType("java.util.EnumSet");
            tableArgs.addImportedType("org.tree.commons.support.mapper.Args");
            tableArgs.addSuperInterface(new FullyQualifiedJavaType(String.format("Args<%s>", modelName)));
            tableArgs.setVisibility(JavaVisibility.PUBLIC);

            Field field;
//            field = new Field();
//            field.setName("TABLE_NAME");
//            field.setType(new FullyQualifiedJavaType("String"));
//            field.setVisibility(JavaVisibility.PUBLIC);
//            field.setStatic(true);
//            field.setFinal(true);
//            field.setInitializationString(String.format("\"%s\"", tableName));
//            tableArgs.addField(field);

            field = new Field();
            field.setName("set");
            field.setType(new FullyQualifiedJavaType(String.format("java.util.EnumSet<%sEnum>", modelName)));
            field.setInitializationString(String.format("EnumSet.noneOf(%sEnum.class)", modelName));
            field.setVisibility(JavaVisibility.PRIVATE);
            tableArgs.addField(field);

            Method method;
            for (IntrospectedColumn introspectedColumn : definition.getValue()) {
                method = new Method(); // setXxx
                method.setReturnType(new FullyQualifiedJavaType(ArgsType));
                method.setName(String.format("set%s", heapUp(introspectedColumn.getJavaProperty())));
                method.setVisibility(JavaVisibility.PUBLIC);
                method.addParameter(new Parameter(new FullyQualifiedJavaType("boolean"), "contained"));
                method.addBodyLine("if (contained)");
                method.addBodyLine(String.format("\tset.add(%sEnum.%s);",
                        modelName, introspectedColumn.getActualColumnName().toUpperCase()));
                method.addBodyLine("else");
                method.addBodyLine(String.format("\tset.remove(%sEnum.%s);",
                        modelName, introspectedColumn.getActualColumnName().toUpperCase()));
                method.addBodyLine("return this;");
                tableArgs.addMethod(method);
            }

            method = new Method(); // setAllTrue
            method.setReturnType(new FullyQualifiedJavaType(ArgsType));
            method.setName("setAllTrue");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addBodyLine(String.format("set = EnumSet.allOf(%sEnum.class);", modelName));
            method.addBodyLine("return this;");
            tableArgs.addMethod(method);

            method = new Method(); // getTableName
            method.setReturnType(new FullyQualifiedJavaType("String"));
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setName("getTableName");
            method.addAnnotation("@Override");
            method.addBodyLine(String.format("return \"%s\";", tableName));
            tableArgs.addMethod(method);

            method = new Method(); // toString
            method.setReturnType(new FullyQualifiedJavaType("String"));
            method.setName("toString");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addAnnotation("@Override");
            method.addBodyLine("if(set.size() == 0)");
            method.addBodyLine(String.format("\treturn \"%s.*\";", tableName));
            method.addBodyLine("StringBuilder sb = new StringBuilder();");
            method.addBodyLine("set.forEach(arg -> sb.append(arg.getName() + \",\"));");
            method.addBodyLine("sb.deleteCharAt(sb.length() - 1);");
            method.addBodyLine("return sb.toString();");
            tableArgs.addMethod(method);

            javaFile = new GeneratedJavaFile(tableArgs, targetProject, new DefaultJavaFormatter());
            javaFiles.add(javaFile);
        }
        return javaFiles;
    }

    private String heapUp(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /****************************** 自定义枚举类 *******************************/

    private static class Enum extends Interface {
        private Enum(String type) {
            super(type);
        }

        // 常量
        private List<String> constants = new ArrayList<>();

        private Enum addConstant(String constant) {
            constants.add(constant);
            return this;
        }

        // 表名
        private String tableName;

        private void setTableName(String tableName) {
            this.tableName = tableName;
        }

        @Override
        public String getFormattedContent() {
            StringBuilder sb = new StringBuilder();

            if (stringHasValue(getType().getPackageName())) {
                sb.append("package ");
                sb.append(getType().getPackageName());
                sb.append(';');
                newLine(sb);
                newLine(sb);
            }

            sb.append("\n\n").append("import org.tree.commons.support.mapper.Searchable;");

            sb.append("\n\n\npublic enum ");
            sb.append(this.getType().getShortName());
            sb.append(" implements Searchable {");

            for (String constant : constants) {
                sb.append(String.format("\n\t%s(\"%s.%s\"),", constant.toUpperCase(), tableName, constant));
            }
            sb.deleteCharAt(sb.length() - 1).append(";");

            // 构造函数
            sb.append("\n\n\t")
                    .append(this.getType().getShortName())
                    .append(" (String name) {")
                    .append("\n\t\t").append("this.name = name;")
                    .append("\n\t").append("}");

            // public static final TABLE_NAME
            // sb.append("\n\n\t").append(String.format("public static final String TABLE_NAME = \"%s\";", tableName));

            // name 及 getName
            sb.append("\n\n\t").append("private String name;")
                    .append("\n\n\t").append("public String getName() {")
                    .append("\n\t\t").append("return name;")
                    .append("\n\t").append("}");

            sb.append("\n").append('}');
            return sb.toString();
        }
    }
}
