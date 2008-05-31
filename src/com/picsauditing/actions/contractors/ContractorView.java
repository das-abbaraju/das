package com.picsauditing.actions.contractors;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.util.Images;

public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, 
			AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
	}
	
	public String execute() throws Exception
	{
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		auditBuilder.buildAudits(this.contractor);
		
		this.subHeading = "Contractor Details";
		
		return SUCCESS;
	}
	
	public int getLogoWidth() throws IOException {
		System.out.println("getLogoWidth for "+contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;
		
		String filename = ServletActionContext.getServletContext().getInitParameter("FTP_DIR")+"logos/"+contractor.getLogoFile();
		System.out.println("filename = "+filename);
		int width = Images.getWidth(filename);
		System.out.println("width = "+width);
		if (width > 300)
			return 300;
		return width;
	}

}
