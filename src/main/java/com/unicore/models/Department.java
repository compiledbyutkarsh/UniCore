package com.unicore.models;

public class Department {
    private int id;
    private String name;
    private String code;
    private String headName;

    public Department() {}

    public Department(String name, String code, String headName) {
        this.name = name;
        this.code = code;
        this.headName = headName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getHeadName() { return headName; }
    public void setHeadName(String headName) { this.headName = headName; }

    @Override
    public String toString() { return code + " - " + name; }
}
