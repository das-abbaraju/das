package com.picsauditing.actions.contractors;

import org.apache.struts2.ServletActionContext;
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
		    String requestHost = this.getRequestHost();
		    
			json.put("name", contractor.getName());
			json.put("address", 
		        contractor.getAddress() + "<br />" +
				contractor.getCity() + ", " +
				contractor.getState().toString() + " " +
				contractor.getZip() + "<br />" +
				contractor.getCountry().toString()
			);
			json.put("description", contractor.getDescription());
			json.put("phone", contractor.getPhone());
			json.put("website", contractor.getWebUrl());
			json.put("logo", requestHost + "/ContractorLogo.action?id=" + contractor.getId());
			
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
