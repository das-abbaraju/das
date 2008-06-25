package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.Billing;
import com.picsauditing.PICS.CertificateBean;
import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.mail.Email;
import com.picsauditing.mail.EmailSender;

public class Cron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	protected OperatorAccountDAO operatorDAO = null;
	protected AppPropertyDAO appPropDao = null;
	
	
	protected final int OPTIMIZE = 1;
	protected final int UPDATE_EXPIRED_CERTS = 2;
	protected final int UPDATE_PAYING_FACILITIES = 3;
	protected final int FLAG_CALCULATION = 4;

	protected boolean flagsOnly = false;
	
	
	public Cron( FlagCalculator2 fc2, OperatorAccountDAO ops, AppPropertyDAO appProps )
	{
		this.flagCalculator = fc2;
		this.operatorDAO = ops;
		this.appPropDao = appProps;
	}
	
	
	
	public String execute() throws Exception {

		StringBuffer report = new StringBuffer();

		boolean hadException = false;
		
		if( !flagsOnly )
		{
			report.append("Running AccountBean optimizer...");
			
			try
			{
				new AccountBean().optimizeDB();
				report.append("SUCCESS.");
			}
			catch( Throwable t )
			{
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.append( sw.toString() );
				report.append("\n\n\n");
			}

			
			report.append("\nRunning AccountBean optimizer...");
			
			try
			{
				new CertificateBean().makeExpiredCertificatesExpiredStatus();
				report.append("SUCCESS.");
			}
			catch( Throwable t )
			{
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.append( sw.toString() );
				report.append("\n\n\n");
			}
			
			
			report.append("\nUpdating Paying Facilities...");
			try
			{
				Facilities facilities =new Facilities(); 
				facilities.setFacilitiesFromDB();
				ServletContext application = ServletActionContext.getServletContext();
				new Billing().updateAllPayingFacilities(application);
				report.append("SUCCESS.");
			}
			catch( Throwable t )
			{
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.append( sw.toString() );
				report.append("\n\n\n");
			}
		}
		
		report.append("\nCalculating Flags...");
		try
		{
			flagCalculator.runAll();
			report.append("SUCCESS.");
		}
		catch( Throwable t )
		{
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			report.append( sw.toString() );
			report.append("\n\n\n");
		}
		
		setMessage("Complete");
		
		sendEmail(report);
		
		return SUCCESS;
	}
	
	protected void sendEmail(StringBuffer report)
	{
		Email email = new Email();
		email.setSubject("Cron job report: " );
		
		email.setBody( report.toString() );
		
		String toAddress = null;
		try
		{
			AppProperty prop = appPropDao.find("admin_email_address");
			toAddress = prop.getValue();
		}
		catch( NoResultException notFound ){}
		
		if( toAddress == null || toAddress.length() == 0 )
		{
			toAddress = "admin@picsauditing.com";
		}
		
		email.setToAddress(toAddress);

		EmailSender sender = new EmailSender();
		
		try
		{
			sender.sendMail(email);
		}
		catch( Exception notMuchWeCanDoButLogIt )
		{
			System.out.println("**********************************");
			System.out.println("Error Sending email from cron job");
			System.out.println("**********************************");
			
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}

	}
	

	public boolean isFlagsOnly() {
		return flagsOnly;
	}
	public void setFlagsOnly(boolean flagsOnly) {
		this.flagsOnly = flagsOnly;
	}
}
