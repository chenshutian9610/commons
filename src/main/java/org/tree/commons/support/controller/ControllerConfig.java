package org.tree.commons.support.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tree.commons.support.Config;

/**
 * @author er_dong_chen
 * @date 2018/12/21
 */
@Component
public class ControllerConfig extends Config {

    @Value("${debug.packageToScan:}")
    private String packageToScan;

    public String getPackageToScan() {
        return packageToScan;
    }

    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
    }
}
