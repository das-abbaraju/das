package com.picsauditing.actions;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.Billing;
import com.picsauditing.PICS.CertificateBean;
import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.FlagCalculator2;

public class Cron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	
	public Cron( FlagCalculator2 fc2 )
	{
		this.flagCalculator = fc2;
	}
	
	
	public String execute() throws Exception {

		ServletContext application = ServletActionContext.getServletContext();
		new AccountBean().optimizeDB();
		new CertificateBean().makeExpiredCertificatesExpiredStatus();
		
		Facilities facilities =new Facilities(); 
		facilities.setFacilitiesFromDB();
		new Billing().updateAllPayingFacilities(application);
		
		flagCalculator.runAll();
		
		setMessage("Complete");
		return SUCCESS;
	}
}
