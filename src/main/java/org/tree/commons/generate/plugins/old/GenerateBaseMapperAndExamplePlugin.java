package org.tree.commons.generate.plugins.old;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/13
 */
public class GenerateBaseMapperAndExamplePlugin extends PluginAdapter {
    private static boolean ifGenerateRootInterface = true;
    private static Interface baseMapper;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String rootInterface = context.getJavaClientGeneratorConfiguration().getTargetPackage() + ".BaseMapper";
        if (ifGenerateRootInterface) {
            baseMapper = new Interface(rootInterface + "<T>");
            baseMapper.setVisibility(JavaVisibility.PUBLIC);
            baseMapper.getMethods().clear();
            baseMapper.getMethods().addAll(_dealWithDeepCopy(interfaze.getMethods(), _getModelName(interfaze)));
            ifGenerateRootInterface = false;
        }
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(2);
        if (baseMapper != null) {
            javaFiles.add(generateBaseMapper());
            javaFiles.add(generateExample());
        }
        return javaFiles;
    }

    /* 生成 BaseMapper 接口 */
    private GeneratedJavaFile generateBaseMapper() {
        String mapperProjectPath = context.getJavaClientGeneratorConfiguration().getTargetProject();
        return new GeneratedJavaFile(baseMapper, mapperProjectPath, new DefaultJavaFormatter());
    }

    /* 生成 Example 接口 */
    private GeneratedJavaFile generateExample() {
        String modelPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String modelProjectPath = context.getJavaModelGeneratorConfiguration().getTargetProject();
        Interface i = new Interface(modelPackage + ".Example<T>");
        i.setVisibility(JavaVisibility.PUBLIC);
        return new GeneratedJavaFile(i, modelProjectPath, new DefaultJavaFormatter());
    }

    /* 深度克隆并处理 */
    private List<Method> _dealWithDeepCopy(List<Method> src, String modelName) {
        List<Method> dest = new ArrayList<>(src.size());
        Method temp;
        Parameter arg;
        String str;
        try {
            for (Method method : src) {
                temp = new Method();
                BeanUtils.copyProperties(method, temp);
                if (temp.getReturnType().getShortName().contains("List"))
                    temp.setReturnType(new FullyQualifiedJavaType("java.util.List<T>"));
                if (temp.getReturnType().getShortName().contains(modelName))
                    temp.setReturnType(new FullyQualifiedJavaType("T"));
                for (Parameter parameter : method.getParameters()) {
                    str = parameter.getType().getFullyQualifiedName();
                    if (str.contains("Example"))
                        str = parameter.getType().getPackageName() + ".Example";
                    if (str.contains(modelName))
                        str = "T";
                    arg = new Parameter(new FullyQualifiedJavaType(str), parameter.getName());
                    temp.getParameters().add(arg);
                }
                dest.add(temp);
            }

        } catch (Exception e) {
        }
        return dest;
    }

    /* 获取 model 类名 */
    private String _getModelName(Interface i) {
        String className = i.getType().getShortName();
        className = className.substring(0, className.indexOf("Mapper"));
        return className;
    }
}
