package com.picsauditing.actions.contractors;

import java.io.IOException;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.util.Images;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;

	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		auditBuilder.setUser(getUser());
		auditBuilder.buildAudits(this.contractor);

		this.subHeading = "Contractor Details";

		return SUCCESS;
	}

	public int getLogoWidth() {
		//System.out.println("getLogoWidth for " + contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
		//System.out.println("filename = " + filename);
		int width = 0;
		try {
			width = Images.getWidth(filename);
		} catch (IOException e) {
			System.out.println("failed to get logo width of " + filename + ": " + e.getMessage());
		}
		//System.out.println("width = " + width);
		if (width > 300)
			return 300;
		return width;
	}
}
