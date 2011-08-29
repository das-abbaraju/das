package com.picsauditing.actions.contractors;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class ContractorPublic extends PicsActionSupport {
	@Autowired
	protected ContractorAccountDAO accountDao;

	private int id;
	private ContractorAccount contractor; 

	@Anonymous
	public String execute() throws Exception {
		contractor = accountDao.find(id);
		return SUCCESS;
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
