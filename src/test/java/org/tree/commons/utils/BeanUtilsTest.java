package org.tree.commons.utils;

import org.testng.annotations.Test;

public class BeanUtilsTest {

    @Test
    public void testGetMap() {
        A a=new A();
        a.setId(40);
        a.setSex("male");
        a.setName("triksi");
        a.setPhone(1008611L);
        System.out.println(BeanUtils.getMap(a));
    }
}

class A{
    private Integer id;
    private String name;
    private String sex;
    private String email;
    private Long phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
}