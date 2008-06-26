package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

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
	protected long startTime = 0L;
	StringBuffer report = null;
	
	protected boolean flagsOnly = false;
	
	
	public Cron( FlagCalculator2 fc2, OperatorAccountDAO ops, AppPropertyDAO appProps )
	{
		this.flagCalculator = fc2;
		this.operatorDAO = ops;
		this.appPropDao = appProps;
	}
	
	
	
	public String execute() throws Exception {

		report = new StringBuffer();

		report.append("Starting Cron Job at: ");
		report.append(new Date().toString());
		report.append("\n\n");
		
		if( !flagsOnly )
		{
			
			try
			{
				startTask( "\nRunning AccountBean optimizer...");
				new AccountBean().optimizeDB();
				endTask();
			}
			catch( Throwable t ) { handleException(t); }

			
			try
			{
				startTask( "\nExpiring Certificates...");
				new CertificateBean().makeExpiredCertificatesExpiredStatus();
				endTask();
			}
			catch( Throwable t ) { handleException(t); }			
			
			
			try
			{
				startTask("\nUpdating Paying Facilities...");
				Facilities facilities =new Facilities(); 
				facilities.setFacilitiesFromDB();
				ServletContext application = ServletActionContext.getServletContext();
				new Billing().updateAllPayingFacilities(application);
				endTask();
			}
			catch( Throwable t ) { handleException(t); }
		}		
		
		
		try
		{
			startTask("\nCalculating Flags...");
			flagCalculator.runAll();
			endTask();
		}
		catch( Throwable t ) { handleException(t); }
	
	

		report.append("\n\n\nCompleted Cron Job at: ");
		report.append(new Date().toString());
		
		sendEmail();

		setMessage("Complete");
		
		return SUCCESS;
	}



	private void handleException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		report.append( t.getMessage() );
		report.append( sw.toString() );
		report.append("\n\n\n");
	}
	
	
	
	protected void endTask()
	{
		report.append("SUCCESS..(");
		report.append(new Long( System.currentTimeMillis() - startTime ).toString());
		report.append(" millis )");
	}
	protected void startTask( String taskName)
	{
		startTime = System.currentTimeMillis();
		report.append(taskName);
	}
	
	protected void sendEmail()
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
