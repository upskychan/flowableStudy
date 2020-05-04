package com.upsky.flowablestudy.variable;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 12345L;
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
