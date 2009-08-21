package com.picsauditing.gwt.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class PicsSuggestion<T> implements Suggestion {

	private T entity;

	PicsSuggestion(T entity) {
		this.entity = entity;
	}

	public String getDisplayString() {
		return entity.toString();
	}

	public String getReplacementString() {
		return "";
	}

	public T getEntity() {
		return entity;
	}
}