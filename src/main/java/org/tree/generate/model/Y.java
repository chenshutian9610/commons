package org.tree.generate.model;

import org.tree.generate.annotation.Column;
import org.tree.generate.annotation.Table;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
@Table(name="yyy")
public class Y {
    @Column(id = true)
    private long id;

    @Column(length = 20)
    private String s;

    @Column(defaultValue = "hello world",comment = "hello china")
    private String str;

}
