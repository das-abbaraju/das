package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;

public class MFilter {
	@Expose
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String removeWildCards(String name){
		if(StringUtils.isEmpty(name))
			return name;

		return name.replace("*","");
	}
}
