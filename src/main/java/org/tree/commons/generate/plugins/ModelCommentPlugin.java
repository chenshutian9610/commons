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
 *
 * <p>  generateComment 可配置, 非空的时候使用自己生成的 Comment, 不建议
 */
public class ModelCommentPlugin extends PluginAdapter {
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
        String generateComment = properties.getProperty("generateComment");
        if (generateComment == null)
            return null;
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
        String annotation;
        String generateComment = properties.getProperty("generateComment");
        annotation = generateComment == null ?
                "org.tree.commons.annotation.Comment" :
                context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Comment";
        topLevelClass.addImportedType(new FullyQualifiedJavaType(annotation));
        topLevelClass.addAnnotation(_getAnnotationString(annotation, introspectedTable.getTableConfiguration().getTableName()));
        Field tableName = new Field("TABLE", new FullyQualifiedJavaType("String"));
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
            if (comment != null && comment.length() != 0)
                field.addAnnotation(_getAnnotationString(annotation, comment));
        }
    }

    /****************************** 辅助方法 *******************************/

    private Map<String, String> _getColumnComment(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getBaseColumns();
        Map<String, String> result = new HashMap<>();
        for (IntrospectedColumn column : columns)
            result.put(column.getJavaProperty(), column.getRemarks());
        List<IntrospectedColumn> keys = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn key : keys)
            result.put(key.getJavaProperty(), key.getRemarks());
        return result;
    }

    private String _getAnnotationString(String className, String value) {
        if (className.contains("."))
            className = className.substring(className.lastIndexOf(".") + 1);
        return "@" + className + "(\"" + value + "\")";
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
