package com.picsauditing.actions;


@SuppressWarnings("serial")
public class FrontendDevelopmentGuide extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String alerts() {
		return "alerts";
	}

	public String buttons() {
		return "buttons";
	}

	public String forms() {
		return "forms";
	}

	public String pills() {
		return "pills";
	}

	public String conventions() {
		return "conventions";
	}

	public String file_structure() {
		return "file-structure";
	}

	public String page_layout() {
		return "page-layout";
	}

	public String style_guide() {
		return "style-guide";
	}
}
