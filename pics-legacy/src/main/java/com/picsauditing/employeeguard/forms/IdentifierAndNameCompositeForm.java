package com.picsauditing.employeeguard.forms;

public class IdentifierAndNameCompositeForm implements PicsForm {

    private int id;
    private String name;

    public IdentifierAndNameCompositeForm() {
    }

    public IdentifierAndNameCompositeForm(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
