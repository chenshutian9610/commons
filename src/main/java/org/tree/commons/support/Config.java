package org.tree.commons.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author er_dong_chen
 * @date 2018/12/21
 */
@Component
public class Config {
    @Value("${debug.enable:false}")
    private boolean debugEnable;

    public boolean isDebugEnable() {
        return debugEnable;
    }

    public void setDebugEnable(boolean debugEnable) {
        this.debugEnable = debugEnable;
    }
}
