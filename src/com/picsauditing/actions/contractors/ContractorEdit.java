package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.util.Images;

public class ContractorEdit extends ContractorActionSupport implements Preparable {
	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}
	
	public void prepare() throws Exception {
		int conID = getParameter("id");
		contractor = accountDao.find(conID);
	}	

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		//findContractor();

		this.subHeading = "Contractor Edit";

		return SUCCESS;
	}

	public int getLogoWidth() throws IOException {
		System.out.println("getLogoWidth for " + contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
		System.out.println("filename = " + filename);
		int width = Images.getWidth(filename);
		System.out.println("width = " + width);
		if (width > 300)
			return 300;
		return width;
	}
}
