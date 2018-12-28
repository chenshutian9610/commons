package org.tree.commons.generate.plugins;

import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/26
 */
public class ConfigurePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


}
