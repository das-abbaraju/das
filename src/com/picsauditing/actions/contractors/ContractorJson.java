package com.picsauditing.actions.contractors;


@SuppressWarnings("serial")
public class ContractorJson extends ContractorActionSupport {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		json = contractor.toJSON(true);

		return JSON;
	}

}
