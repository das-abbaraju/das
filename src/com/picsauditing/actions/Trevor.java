package com.picsauditing.actions;

import java.sql.SQLException;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private ContractorAccountDAO accountDAO;

	public Trevor(ContractorAccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	public String execute() throws SQLException {

		System.out.println("Querying the first time");
		ContractorAudit t = (ContractorAudit) accountDAO.find(ContractorAudit.class, 1688);
		
		return SUCCESS;
	}

}
