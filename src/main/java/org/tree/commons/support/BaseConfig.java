package org.tree.commons.support;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author er_dong_chen
 * @date 2018/12/21
 */
public abstract class BaseConfig {
    @Value("${debug.enable:false}")
    protected boolean debugEnable;
}
