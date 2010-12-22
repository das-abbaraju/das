package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorValidate extends ContractorActionSupport {
	private String companyName;
	private String taxId;
	private String country;

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

			return BLANK;
		}

		if ("taxId".equals(button)) {
			if (!Strings.isEmpty(taxId) && !Strings.isEmpty(country)) {
				ContractorAccount con = accountDao.findTaxID(taxId, country);
				if (con != null) {
					addActionError(taxId + " already exists in " + con.getCountry().getName()
							+ ". Please contact a PICS representative at 949-936-4598.");
				}
			}

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

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
