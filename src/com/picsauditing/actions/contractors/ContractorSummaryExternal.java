package com.picsauditing.actions.contractors;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class ContractorSummaryExternal extends PicsActionSupport {
	@Autowired
	protected ContractorAccountDAO accountDao;

	private int id;
	private ContractorAccount contractor;
	protected JSONObject json = new JSONObject();
	
	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() throws Exception {
		
		contractor = accountDao.find(id);
		
		if (contractor != null) {
			json.put("name", contractor.getName());
			json.put("image", getFtpDir() + "/logos/" + contractor.getLogoFile());
			json.put("address", contractor.getAddress() + " " +
								contractor.getCity() + ", " +
								contractor.getState().toString() + " " +
								contractor.getZip() + " " +
								contractor.getCountry().toString()
					);
			json.put("description", contractor.getDescription());
			json.put("phone", contractor.getPhone());
			json.put("website", contractor.getWebUrl());
			return JSON;
		}
		
		return SUCCESS;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

}
