package com.picsauditing.actions;


@SuppressWarnings("serial")
public class FrontendDevelopmentGuide extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String technology() {
		return "technology";
	}

	public String file_structure() {
		return "file-structure";
	}

	public String style_guide() {
		return "style-guide";
	}

	public String page_layout() {
		return "page-layout";
	}

	public String component() {
		return "component";
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

	public String css_javascript_conventions() {
		return "css-javascript-conventions";
	}

	public String html_conventions() {
		return "html-conventions";
	}

	public String file_structure_conventions() {
		return "file-structure-conventions";
	}
}