package com.picsauditing.employeeguard.viewmodel.contractor;

public class ProjectModel {

    private int id;
    private String name;

    public ProjectModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
