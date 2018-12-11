package org.tree.generate.plugins;

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
 */
public class ModelPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(1);
        Annotation annotation = new Annotation(context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Comment");
        annotation.setVisibility(JavaVisibility.PUBLIC);
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

    /****************************** 主方法 *******************************/

    private void generateAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String annotation = context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Comment";
        topLevelClass.addAnnotation(_getAnnotationString(annotation, introspectedTable.getTableConfiguration().getTableName()));
        Field tableName = new Field("tableName", new FullyQualifiedJavaType("String"));
        tableName.setVisibility(JavaVisibility.PUBLIC);
        tableName.setFinal(true);
        tableName.setStatic(true);
        tableName.setInitializationString("\"" + introspectedTable.getTableConfiguration().getTableName() + "\"");
        topLevelClass.addField(tableName);
        Map<String, String> comments = _getColumnComment(introspectedTable);
        List<Field> fields = topLevelClass.getFields();
        String comment;
        for (Field field : fields) {
            comment = comments.get(field.getName());
            if (comment != null)
                field.addAnnotation(_getAnnotationString(annotation, comment));
        }
    }

    /****************************** 辅助方法 *******************************/

    private Map<String, String> _getColumnComment(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getBaseColumns();
        Map<String, String> result = new HashMap<>();
        for (IntrospectedColumn column : columns)
            result.put(_deal(column.getActualColumnName()), column.getRemarks());
        return result;
    }

    private String _getAnnotationString(String className, String value) {
        if (className.contains("."))
            className = className.substring(className.lastIndexOf(".") + 1);
        return "@" + className + "(\"" + value + "\")";
    }

    /* 下划线转驼峰 */
    private String _deal(String str) {
        if (str.contains("_")) {
            String[] array = str.split("_");
            StringBuilder sb = new StringBuilder(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(array[i].substring(0, 1).toUpperCase());
                sb.append(array[i].substring(1, array[i].length()));
            }
            return new String(sb);
        }
        return str;
    }

    /****************************** 内部类 *******************************/

    private static class Annotation extends Interface {
        private Annotation(String className) {
            super(className);
        }

        @Override
        public String getFormattedContent() {

            StringBuilder sb = new StringBuilder();

            for (String commentLine : getFileCommentLines()) {
                sb.append(commentLine);
                newLine(sb);
            }

            if (stringHasValue(getType().getPackageName())) {
                sb.append("package ");
                sb.append(getType().getPackageName());
                sb.append(';');
                newLine(sb);
                newLine(sb);
            }

            for (String staticImport : getStaticImports()) {
                sb.append("import static ");
                sb.append(staticImport);
                sb.append(';');
                newLine(sb);
            }

            if (getStaticImports().size() > 0) {
                newLine(sb);
            }

            Set<String> importStrings = calculateImports(getImportedTypes());
            for (String importString : importStrings) {
                sb.append(importString);
                newLine(sb);
            }

            if (importStrings.size() > 0) {
                newLine(sb);
            }

            int indentLevel = 0;

            addFormattedJavadoc(sb, indentLevel);
            addFormattedAnnotations(sb, indentLevel);

            sb.append(getVisibility().getValue());

            if (isStatic()) {
                sb.append("static ");
            }

            if (isFinal()) {
                sb.append("final ");
            }

            sb.append("@interface ");
            sb.append(getType().getShortName());

            if (getSuperInterfaceTypes().size() > 0) {
                sb.append(" extends ");

                boolean comma = false;
                for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
                    if (comma) {
                        sb.append(", ");
                    } else {
                        comma = true;
                    }

                    sb.append(JavaDomUtils.calculateTypeName(this, fqjt));
                }
            }

            sb.append(" {");
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
