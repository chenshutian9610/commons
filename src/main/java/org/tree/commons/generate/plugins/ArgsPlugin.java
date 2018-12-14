package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class ArgsPlugin extends PluginAdapter {
    private String modelName;
    private List<String> modelFileds = new ArrayList<>();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelName = topLevelClass.getType().getFullyQualifiedName();
        List<Field> fields = topLevelClass.getFields();
        for (Field field : fields)
            modelFileds.add(field.getName());
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        String type = modelName + "Args";
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addImportedType("org.tree.commons.support.mapper.Args");
        topLevelClass.setSuperClass("Args");

        Field field;
        Method method;
        InitializationBlock initializationBlock = new InitializationBlock();
        for (String modelField : modelFileds) {
            if ("tableName".equals(modelField))
                continue;

            field = new Field(modelField, new FullyQualifiedJavaType("Arg"));
            field.setInitializationString(String.format("new Arg(\"%s\")", modelField));
            field.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(field);

            initializationBlock.addBodyLine(String.format("args.add(%s);", modelField));

            method = new Method(String.format("set%s", _headUp(modelField)));
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(new FullyQualifiedJavaType(type));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("boolean"), "contained"));
            method.addBodyLine(String.format("this.%s.setContained(contained);", modelField));
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

        List<GeneratedJavaFile> javaFiles = new ArrayList<>(1);
        GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
        javaFiles.add(javaFile);
        return javaFiles;
    }

    private String _headUp(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
