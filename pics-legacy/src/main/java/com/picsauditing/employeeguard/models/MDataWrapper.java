package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

public class MDataWrapper {

	@Expose
	private Object response;

	public MDataWrapper(Object response) {
		this.response = response;
	}

	public Object getResponse() {
		return response;
	}

}
