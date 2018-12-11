package org.tree.generate.annotation;

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

}
