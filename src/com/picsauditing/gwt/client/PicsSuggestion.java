package com.picsauditing.gwt.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class PicsSuggestion<T> implements Suggestion {

	private T entity;

	PicsSuggestion(T entity) {
		this.entity = entity;
	}

	public String getDisplayString() {
		// TODO Auto-generated method stub
		return entity.toString();
	}

	public String getReplacementString() {
		// TODO Auto-generated method stub
		return entity.toString();
	}

	public T getEntity() {
		return entity;
	}
}