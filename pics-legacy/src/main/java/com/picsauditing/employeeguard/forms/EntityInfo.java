package com.picsauditing.employeeguard.forms;

public class EntityInfo implements Comparable<EntityInfo> {

    private int id;
    private String name;

    public EntityInfo() {
    }

    public EntityInfo(int id, String name) {
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

	@Override
	public int compareTo(EntityInfo that) {
		return this.getName().compareToIgnoreCase(that.getName());
	}
}
