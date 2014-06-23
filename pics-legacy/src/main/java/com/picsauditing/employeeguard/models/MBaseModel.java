package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

public class MBaseModel {
	@Expose
	protected Integer id;
	@Expose
	protected String name;
	@Expose
	protected String description;

	//-- Getters/Setters

	public Integer getId() {
		return this.id;
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

}
