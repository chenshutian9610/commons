package org.tree.generate.model;

import org.tree.generate.annotation.Column;
import org.tree.generate.annotation.Table;

import java.util.Date;

/**
 * @author er_dong_chen
 * @date 2018/12/11
 */
@Table(name = "xxx", comment = "my xxx")
public class X {
    @Column(id = true)
    long id;
    int i;
    byte b;
    boolean bool;
    Date date;
    String s;
    char c;
}
