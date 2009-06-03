package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

//import com.picsauditing.PicsRunListener;
//import com.picsauditing.PicsTestSuite;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class JUnitNightly extends PicsActionSupport {

	static protected User system = new User(User.SYSTEM);
	protected AppPropertyDAO appPropDao = null;

	protected StringBuffer report;

	public JUnitNightly(AppPropertyDAO appProps) {
		this.appPropDao = appProps;
	}

	public String execute() throws Exception {

		report = new StringBuffer();

//		JUnitCore core = new JUnitCore();
//		List<Failure> failures = new ArrayList<Failure>();
//		PicsRunListener listener = new PicsRunListener(failures);
//		core.addListener(listener);
//		core.run(new Class[] { PicsTestSuite.class });
//		core.removeListener(listener);
//		
//		report.append("There are ").append(failures.size()).append(" Failures.<br/><br/>");
//		
//		for (Failure f : failures) {
//			report.append(f.getTestHeader()).append("<br/>");
//			report.append(f.getMessage()).append("<br/>");
//			report.append(f.getTrace()).append("<br/><br/>");
//		}
//		
		addActionMessage(report.toString());

		return SUCCESS;
	}

	protected void sendEmail() {
		String toAddress = null;
		try {
			AppProperty prop = appPropDao.find("admin_email_address");
			toAddress = prop.getValue();
		} catch (NoResultException notFound) {
		}

		if (toAddress == null || toAddress.length() == 0) {
			toAddress = "admin@picsauditing.com";
		}

		try {
			EmailSender.send(toAddress, "Cron job report", report.toString());
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("**********************************");
			System.out.println("Error Sending email from cron job");
			System.out.println("**********************************");

			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}

	}

}
