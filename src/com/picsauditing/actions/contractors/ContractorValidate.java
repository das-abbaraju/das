package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorValidate extends ContractorActionSupport {
	protected String companyName;

	protected UserDAO userDAO;
	protected ContractorValidator contractorValidator;

	public ContractorValidate(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorValidator contractorValidator, UserDAO userDAO) {
		super(accountDao, auditDao);
		this.contractorValidator = contractorValidator;
		this.userDAO = userDAO;
		this.subHeading = "New Contractor Information";
	}

	public String execute() throws Exception {

		if ("name".equals(button)) {

			if (Strings.isEmpty(companyName))
				return BLANK;

			String nameIndex = Strings.indexName(companyName);
			List<ContractorAccount> matches = accountDao.findWhere("nameIndex = ?", nameIndex);

			if (matches.size() > 0)
				addActionError(companyName + " is already in use. Please contact a PICS representative.");
			else
				addActionMessage(companyName + " is available.");

			return BLANK;
		}

		return SUCCESS;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
