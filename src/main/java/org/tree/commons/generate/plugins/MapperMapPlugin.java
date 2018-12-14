package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.tree.commons.support.mapper.MapperMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/13
 */
public class MapperMapPlugin extends PluginAdapter {
    private static List<String> mapperList = new ArrayList<>();

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        mapperList.add(interfaze.getType().getShortName());
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(1);
        javaFiles.add(generateMapperMap());
        return javaFiles;
    }

    /* 生成 MapperMap 类 */
    private GeneratedJavaFile generateMapperMap() {
        String mapperPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        String mapperProjectPath = context.getJavaClientGeneratorConfiguration().getTargetProject();
        TopLevelClass mapperMap = new TopLevelClass(mapperPackage + ".LocalMapperMap");
        mapperMap.setVisibility(JavaVisibility.PUBLIC);
        mapperMap.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        mapperMap.addImportedType("org.springframework.stereotype.Component");
        mapperMap.addImportedType("javax.annotation.PostConstruct");
        mapperMap.addImportedType(MapperMap.class.getName());
        mapperMap.addAnnotation("@Component");
        mapperMap.setSuperClass("MapperMap");
//        mapperMap.addImportedType("java.util.HashMap");
//        mapperMap.addImportedType("java.util.Map");
//        Field field = new Field("map", new FullyQualifiedJavaType("java.util.Map"));
//        field.setInitializationString("new HashMap<String, BaseMapper>()");
//        field.setStatic(true);
//        mapperMap.addField(field);
        Method method = new Method("init");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@PostConstruct");
        Field temp;
        for (String str : mapperList) {
            temp = new Field(_getInstanceName(str), new FullyQualifiedJavaType(str));
            temp.addAnnotation("@Autowired");
            mapperMap.addField(temp);
            method.addBodyLine("put(\"" + _getModelName(str) + "\", " + _getInstanceName(str) + ");");
        }
        mapperMap.addMethod(method);
        return new GeneratedJavaFile(mapperMap, mapperProjectPath, new DefaultJavaFormatter());
    }

    /* 获取 model 类名 */
    private String _getModelName(String mapperName) {
        if (mapperName.contains("Mapper"))
            return mapperName.substring(0, mapperName.indexOf("Mapper"));
        return mapperName;
    }

    /* 开头小写 */
    private String _getInstanceName(String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1, className.length());
    }

}
