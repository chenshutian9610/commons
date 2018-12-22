package org.tree.commons.generate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author er_dong_chen
 * @date 2018/12/11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String name() default "";

    String comment() default "";

    String meta() default "ENGINE=InnoDB DEFAULT CHARSET=utf8";

    /* 为 false 时只扫描但不生成表 */
    boolean generate() default true;
}
