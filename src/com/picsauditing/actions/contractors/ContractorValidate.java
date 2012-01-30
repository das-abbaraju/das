package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.Anonymous;
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

	public ContractorValidate(ContractorValidator contractorValidator, UserDAO userDAO) {
		this.contractorValidator = contractorValidator;
		this.userDAO = userDAO;
		this.subHeading = "New Contractor Information";
	}

	@Anonymous
	public String execute() throws Exception {

		if ("name".equals(button)) {

			if (Strings.isEmpty(companyName))
				return BLANK;

			String nameIndex = Strings.indexName(companyName);
			List<ContractorAccount> matches = contractorAccountDao.findWhere("nameIndex = ?", nameIndex);

			if (matches.size() > 0)
				addActionError(getText("Status.CompanyInUse", new Object[] { companyName }));

			return BLANK;
		}

		if ("taxId".equals(button)) {
			if (!Strings.isEmpty(taxId) && !Strings.isEmpty(country)) {
				
				if("CA".equals(country) && taxId.length() != 15){
					addActionError(getText("Status.TaxIdLength.CA"));
					return BLANK;
				} else if(!"CA".equals(country) && taxId.length() != 9){
					addActionError(getText("Status.TaxIdLength.US"));
					return BLANK;
				}
				
				ContractorAccount con = contractorAccountDao.findTaxID(taxId.substring(0, 9), country);
				if (con != null) {
					if (con.getCountry().isCanada())
						addActionError(getText("Status.TaxIdInUse.Canada", new Object[] { taxId, con.getTaxId(),
								Strings.getPicsTollFreePhone(con.getCountry().getIsoCode()) }));
					else
						addActionError(getText("Status.TaxIdInUse", new Object[] { taxId, con.getCountry().getName() }));
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
