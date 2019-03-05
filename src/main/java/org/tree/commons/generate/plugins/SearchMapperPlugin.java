package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2019/3/5
 */
public class SearchMapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>(1);
        String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        Interface searchMapper = new Interface(targetPackage + ".SearchMapper");
        searchMapper.addImportedType(new FullyQualifiedJavaType("org.tree.commons.support.mapper.UnionSearchMapper"));
        searchMapper.addSuperInterface(new FullyQualifiedJavaType("UnionSearchMapper"));
        searchMapper.setVisibility(JavaVisibility.PUBLIC);
        GeneratedJavaFile javaFile = new GeneratedJavaFile(searchMapper, targetPackage, new DefaultJavaFormatter());
        javaFiles.add(javaFile);
        return javaFiles;
    }
}
