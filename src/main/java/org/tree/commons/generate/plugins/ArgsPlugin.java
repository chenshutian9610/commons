package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class ArgsPlugin extends PluginAdapter {
    //    private String modelName;
//    private List<String> modelFileds = new ArrayList<>();
    private Map<String, List<String>> modelArgs = new HashMap<>();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String modelName = topLevelClass.getType().getFullyQualifiedName();
        List<Field> fields = topLevelClass.getFields();
        List<String> modelFileds = new ArrayList<>();
        for (Field field : fields)
            modelFileds.add(field.getName());
        modelArgs.put(modelName, modelFileds);
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
        for (Map.Entry<String, List<String>> entry : modelArgs.entrySet()) {
            String type = entry.getKey() + "Args";
            topLevelClass = new TopLevelClass(type);
            topLevelClass.setVisibility(JavaVisibility.PUBLIC);
            topLevelClass.addImportedType("org.tree.commons.support.mapper.Args");
            topLevelClass.setSuperClass("Args");

            initializationBlock = new InitializationBlock();
            for (String modelField : entry.getValue()) {
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

            javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
            javaFiles.add(javaFile);
        }
        return javaFiles;
    }

    private String _headUp(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
