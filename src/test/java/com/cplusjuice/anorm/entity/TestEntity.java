package com.cplusjuice.anorm.entity;

import com.cplusjuice.anorm.bean.Presents;

@Presents("testentity")
public class TestEntity {
    private Integer id;
    private String columnOne;
    private String columnTwo;

    public TestEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColumnOne() {
        return columnOne;
    }

    public void setColumnOne(String columnOne) {
        this.columnOne = columnOne;
    }

    public String getColumnTwo() {
        return columnTwo;
    }

    public void setColumnTwo(String columnTwo) {
        this.columnTwo = columnTwo;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", columnOne='" + columnOne + '\'' +
                ", columnTwo='" + columnTwo + '\'' +
                '}';
    }
}