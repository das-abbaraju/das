package com.picsauditing.employeeguard.forms.operator;

public class RoleInfo implements Comparable<RoleInfo> {

    private int id;
    private String name;

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

	@Override
	public int compareTo(RoleInfo that) {
		return this.getName().compareTo(that.getName());
	}
}
