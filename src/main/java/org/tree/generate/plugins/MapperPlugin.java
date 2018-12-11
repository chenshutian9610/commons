package org.tree.generate.plugins;

import org.apache.commons.beanutils.BeanUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/4
 */
public class MapperPlugin extends PluginAdapter {
    private static boolean ifGenerateRootInterface = true; // 是否生成 rootInterface
    private static List<String> mapperList = new ArrayList<>();
    private static Interface baseMapper;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override // 修改 Mapper
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateMapperMethod(interfaze);
        try {
            generateMapperRootInterface(interfaze);
        } catch (Exception e) {
        }
        mapperList.add(interfaze.getType().getShortName());
        return true;
    }

    @Override // 修改 XML
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        generateXmlElement(document);
        return true;
    }

    @Override // 修改 Example
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateExampleRootInterface(topLevelClass);
        return true;
    }

    @Override // 生成 BaseMapper 和 Example
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(3);
        if (baseMapper != null) {
            javaFiles.add(generateBaseMapper());
            javaFiles.add(generateExample());
            javaFiles.add(generateMapperMap());
        }
        return javaFiles;
    }

    /****************************** 主方法 *******************************/

    /* 令所有 Mapper 继承 BaseMapper */
    private void generateMapperRootInterface(Interface i) throws Exception {
        String rootInterface = context.getJavaClientGeneratorConfiguration().getTargetPackage() + ".BaseMapper";
        i.addSuperInterface(new FullyQualifiedJavaType(_getGenericString(i, rootInterface)));
        if (ifGenerateRootInterface) {
            baseMapper = new Interface(rootInterface + "<T>");
            baseMapper.setVisibility(JavaVisibility.PUBLIC);
            baseMapper.getMethods().clear();
            baseMapper.getMethods().addAll(_dealWithDeepCopy(i.getMethods(), _getModelName(i)));
            ifGenerateRootInterface = false;
        }
    }

    /* 令所有 Mapper 添加一个方法 */
    private void generateMapperMethod(Interface i) {
        Method method = _newMapperMethod("java.util.List<" + _getModelName(i) + ">", "selectParams",
                new Parameter(new FullyQualifiedJavaType("java.lang.String"), "sql", "@Param(\"sql\")"));
        i.addMethod(method);
    }

    /* 令所有 xml 添加一个标签 */
    private void generateXmlElement(Document document) {
        XmlElement element = _newXmlElement("select", "selectParams", "${sql}");
        document.getRootElement().addElement(element);
    }

    /* 令所有 Example 实现 Example */
    private void generateExampleRootInterface(TopLevelClass topLevelClass) {
        String extend = context.getJavaModelGeneratorConfiguration().getTargetPackage() + ".Example";
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(_getGenericString(topLevelClass, extend)));
    }

    /* 生成 Example 接口 */
    private GeneratedJavaFile generateExample() {
        String modelPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String modelProjectPath = context.getJavaModelGeneratorConfiguration().getTargetProject();
        Interface i = new Interface(modelPackage + ".Example<T>");
        i.setVisibility(JavaVisibility.PUBLIC);
        return new GeneratedJavaFile(i, modelProjectPath, new DefaultJavaFormatter());
    }

    /* 生成 BaseMapper 接口 */
    private GeneratedJavaFile generateBaseMapper() {
        String mapperProjectPath = context.getJavaClientGeneratorConfiguration().getTargetProject();
        return new GeneratedJavaFile(baseMapper, mapperProjectPath, new DefaultJavaFormatter());
    }

    /* 生成 MapperMap 类 */
    private GeneratedJavaFile generateMapperMap() {
        String mapperPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        String mapperProjectPath = context.getJavaClientGeneratorConfiguration().getTargetProject();
        TopLevelClass mapperMap = new TopLevelClass(mapperPackage + ".MapperMap");
        mapperMap.setVisibility(JavaVisibility.PUBLIC);
        mapperMap.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        mapperMap.addImportedType("org.springframework.stereotype.Component");
        mapperMap.addImportedType("javax.annotation.PostConstruct");
        mapperMap.addImportedType("java.util.HashMap");
        mapperMap.addImportedType("java.util.Map");
        mapperMap.addAnnotation("@Component");
        Field field = new Field("map", new FullyQualifiedJavaType("java.util.Map"));
        field.setInitializationString("new HashMap<String,BaseMapper>()");
        field.setStatic(true);
        mapperMap.addField(field);
        Method method = new Method("init");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@PostConstruct");
        Field temp;
        for (String str : mapperList) {
            temp = new Field(_getInstanceName(str), new FullyQualifiedJavaType(str));
            temp.addAnnotation("@Autowired");
            mapperMap.addField(temp);
            method.addBodyLine("map.put(\"" + _getModelName(str) + "\", " + _getInstanceName(str) + ");");
        }
        mapperMap.addMethod(method);
        return new GeneratedJavaFile(mapperMap, mapperProjectPath, new DefaultJavaFormatter());
    }

    /****************************** 辅助方法 *******************************/

    /* 在 xml 中添加一个标签 */
    private XmlElement _newXmlElement(String label, String id, String content) {
        XmlElement element = new XmlElement(label);
        element.addAttribute(new Attribute("id", id));
        element.addAttribute(new Attribute("parameterType", "java.lang.String"));
        element.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        element.addElement(new TextElement(content));
        context.getCommentGenerator().addComment(element);
        return element;
    }

    /* 在 Mapper 中添加一个方法 */
    private Method _newMapperMethod(String returnType, String name, Parameter... params) {
        Method method = new Method();
        method.setName(name);
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        for (Parameter param : params)
            method.getParameters().add(param);
        return method;
    }

    /* 获取泛型格式的类名 */
    private String _getGenericString(Interface i, String className) {
        String mapper = i.getType().getShortName();
        className = className + "<" + mapper.substring(0, mapper.indexOf("Mapper")) + ">";
        return className;
    }

    /* 获取泛型格式的类名 */
    private String _getGenericString(TopLevelClass topLevelClass, String className) {
        String example = topLevelClass.getType().getShortName();
        className = className + "<" + example.substring(0, example.indexOf("Example")) + ">";
        return className;
    }

    /* 获取 model 类名 */
    private String _getModelName(Interface i) {
        String className = i.getType().getShortName();
        className = className.substring(0, className.indexOf("Mapper"));
        return className;
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

    /* 深度克隆并处理 */
    private List<Method> _dealWithDeepCopy(List<Method> src, String modelName) throws Exception {
        List<Method> dest = new ArrayList<>(src.size());
        Method temp;
        Parameter arg;
        String str;
        for (Method method : src) {
            temp = (Method) BeanUtils.cloneBean(method);
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
        return dest;
    }
}