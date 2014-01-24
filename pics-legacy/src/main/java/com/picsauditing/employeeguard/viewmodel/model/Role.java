package com.picsauditing.employeeguard.viewmodel.model;

public class Role {

    private final int id;
    private final String name;

    public Role(final int id, final String name) {
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
