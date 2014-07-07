package com.picsauditing.struts.controller.contractor;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class AdvancedEditController extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

    public String blank() throws Exception {
        return BLANK;
    }

    public String result() {
        jsonString = "[{id:12345,name:'Hello World'}]";

        return JSON_STRING;
    }

	public String contractorMenu() throws Exception {
		return "contractor-menu";
	}

	public String operatorMenu() throws Exception {
		return "operator-menu";
	}

	public String picsMenu() throws Exception {
		return "pics-menu";
	}
}