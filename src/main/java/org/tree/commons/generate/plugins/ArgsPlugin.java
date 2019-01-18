package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.*;

/**
 * @author er_dong_chen
 * @date 18-12-14
 *
 * <p> 服务于 QuerySelectiveByExamplePlugin
 */
public class ArgsPlugin extends PluginAdapter {
    private Map<String, Map<String, String>> modelArgs = new HashMap<>();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String modelName = topLevelClass.getType().getFullyQualifiedName();
        Map<String, String> modelFields = new LinkedHashMap<>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns())
            /* 类中属性名：数据库中字段名*/
            modelFields.put(column.getJavaProperty(), column.getActualColumnName());
        modelArgs.put(modelName, modelFields);
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>();
        GeneratedJavaFile javaFile;
        TopLevelClass topLevelClass;
        InitializationBlock initializationBlock;
        Field field;
        Method method;
        for (Map.Entry<String, Map<String, String>> entry : modelArgs.entrySet()) {
            String type = entry.getKey() + "Args";
            topLevelClass = new TopLevelClass(type);
            topLevelClass.setVisibility(JavaVisibility.PUBLIC);
            topLevelClass.addImportedType("org.tree.commons.support.mapper.Args");
            topLevelClass.setSuperClass("Args");

            initializationBlock = new InitializationBlock();
            for (Map.Entry<String, String> modelField : entry.getValue().entrySet()) {
                if ("TABLE".equals(modelField))
                    continue;

                field = new Field(modelField.getKey(), new FullyQualifiedJavaType("Arg"));
                field.setInitializationString(String.format("new Arg(\"%s\")", modelField.getValue()));
                field.setVisibility(JavaVisibility.PRIVATE);
                topLevelClass.addField(field);

                initializationBlock.addBodyLine(String.format("args.add(%s);", modelField.getKey()));

                method = new Method(String.format("set%s", _headUp(modelField.getKey())));
                method.setVisibility(JavaVisibility.PUBLIC);
                method.setReturnType(new FullyQualifiedJavaType(type));
                method.addParameter(new Parameter(new FullyQualifiedJavaType("boolean"), "contained"));
                method.addBodyLine(String.format("this.%s.setContained(contained);", modelField.getKey()));
                method.addBodyLine("return this;");
                topLevelClass.addMethod(method);
            }
            topLevelClass.addInitializationBlock(initializationBlock);

            method = new Method("setAllTrue");
            method.setReturnType(new FullyQualifiedJavaType(type));
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addBodyLine("for (Arg arg : args)");
            method.addBodyLine("\targ.setContained(true);");
            method.addBodyLine("return this;");
            topLevelClass.addMethod(method);

//            method = new Method("init");
//            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "args", true));
//            method.setReturnType(new FullyQualifiedJavaType(type));
//            method.setStatic(true);
//            method.setVisibility(JavaVisibility.PUBLIC);
//            type = type.substring(type.lastIndexOf(".") + 1);
//            method.addBodyLine(String.format("%s result = new %s();", type, type));
//            method.addBodyLine("result.init0(args);");
//            method.addBodyLine("return result;");
//            topLevelClass.addMethod(method);

            javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
            javaFiles.add(javaFile);
        }
        return javaFiles;
    }

    private String _headUp(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
