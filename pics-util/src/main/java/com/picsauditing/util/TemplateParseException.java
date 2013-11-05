package com.picsauditing.util;

import com.picsauditing.exception.PicsException;

public class TemplateParseException extends PicsException {
	private String template;

	public TemplateParseException(String message, Throwable cause, String template) {
		super(message, cause);
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

}
