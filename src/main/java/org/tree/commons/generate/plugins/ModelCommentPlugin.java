package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.*;

import static org.mybatis.generator.api.dom.OutputUtilities.*;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * @author er_dong_chen
 * @date 2018/12/4
 * <p>
 * 使用 @Comment 为所有 model 类添加注释
 */
public class ModelCommentPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    /****************************** 为 model 类添加注释 *******************************/

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String annotation = "org.tree.commons.annotation.Comment";
        try {
            Class.forName("org.tree.commons.annotation.Comment");
        } catch (ClassNotFoundException e) {
            annotation = context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Comment";
        }
        topLevelClass.addImportedType(new FullyQualifiedJavaType(annotation));

//        topLevelClass.addAnnotation(getAnnotationString(annotation, introspectedTable.getTableConfiguration().getTableName()));

//        Field tableName = new Field("TABLE", new FullyQualifiedJavaType("String"));
//        tableName.setVisibility(JavaVisibility.PUBLIC);
//        tableName.setFinal(true);
//        tableName.setStatic(true);
//        tableName.setInitializationString("\"" + introspectedTable.getTableConfiguration().getTableName() + "\"");
//        topLevelClass.addField(tableName);

        Map<String, String> comments = getColumnComment(introspectedTable);
        List<Field> fields = topLevelClass.getFields();
        String comment;
        for (Field field : fields) {
            comment = comments.get(field.getName());
            if (comment != null && comment.length() != 0)
                field.addAnnotation(getAnnotationString(annotation, comment));
        }
        return true;
    }

    /* 获取所有字段的注释 */
    private Map<String, String> getColumnComment(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getBaseColumns();
        Map<String, String> result = new HashMap<>();
        for (IntrospectedColumn column : columns)
            result.put(column.getJavaProperty(), column.getRemarks());
        List<IntrospectedColumn> keys = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn key : keys)
            result.put(key.getJavaProperty(), key.getRemarks());
        return result;
    }

    /* 获取 @Comment("$VALUE") 的字符串 */
    private String getAnnotationString(String className, String value) {
        if (className.contains("."))
            className = className.substring(className.lastIndexOf(".") + 1);
        return "@" + className + "(\"" + value + "\")";
    }


    /******************** org.tree.commons.annotation.Comment 不存在时，会生成 @Comment 类 *********************/

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        try {
            Class.forName("org.tree.commons.annotation.Comment");
            return null;
        } catch (ClassNotFoundException e) {
            List<GeneratedJavaFile> javaFiles = new ArrayList<>(1);
            Annotation annotation = new Annotation(context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Comment");
            annotation.addImportedType(new FullyQualifiedJavaType("java.lang.annotation.*"));
            annotation.addAnnotation("@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})");
            annotation.addAnnotation("@Retention(RetentionPolicy.RUNTIME)");
            Method method = new Method("value");
            method.setReturnType(new FullyQualifiedJavaType("java.lang.String"));
            annotation.addMethod(method);
            GeneratedJavaFile javaFile = new GeneratedJavaFile(annotation, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
            javaFiles.add(javaFile);
            return javaFiles;
        }
    }

    /****************************** 自定义注解类 *******************************/

    private static class Annotation extends Interface {
        private Annotation(String className) {
            super(className);
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

            Set<String> importStrings = calculateImports(getImportedTypes());
            for (String importString : importStrings) {
                sb.append(importString);
                newLine(sb);
            }
            newLine(sb);

            int indentLevel = 0;
            addFormattedJavadoc(sb, indentLevel);
            addFormattedAnnotations(sb, indentLevel);

            sb.append("public @interface ").append(getType().getShortName()).append(" {");
            indentLevel++;

            Iterator<Method> mtdIter = getMethods().iterator();
            while (mtdIter.hasNext()) {
                newLine(sb);
                Method method = mtdIter.next();
                sb.append(method.getFormattedContent(indentLevel, true, this));
                if (mtdIter.hasNext()) {
                    newLine(sb);
                }
            }

            indentLevel--;
            newLine(sb);
            javaIndent(sb, indentLevel);
            sb.append('}');

            return sb.toString();
        }
    }
}
