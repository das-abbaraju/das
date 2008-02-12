package com.picsauditing.PICS;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;

import java.text.Format;
import java.util.Map;
import java.util.Properties;
import java.sql.*;

import javax.activation.*;

import com.picsauditing.access.Permissions;
import com.picsauditing.access.User;
import com.picsauditing.domain.CertificateDO;

public class EmailBean extends DataBean{
	static final boolean IS_TESTING = false;
	static final String EMAIL_OUT = "c:\\emailOut.txt";

	private static final String EMAIL_ACCOUNT = "info@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP2 = "info2@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP3 = "info3@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP4 = "info4@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP5 = "info5@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP6 = "info6@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP7 = "info7@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP8 = "info8@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP9 = "info9@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP10 = "info10@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP11 = "info11@picsauditing.com";
	private static final String EMAIL_ACCOUNT_BACKUP12 = "info12@picsauditing.com";
	private static final String PASSWORD = "e3r4t5";
	static final char endl = '\n';
	static final char endl2 = '\r';
	private static String rootPath = null;
	public static String FROM_INFO = "PICS <"+"info@picsauditing.com>";

	public static final String EMAIL_FOOTER =
		"PICS - Pacific Industrial Contractor Screening"+endl2+endl+
		"P.O. Box 51387"+endl2+endl+
		"Irvine CA 92619-1387"+endl2+endl+
		"(949)387-1940"+endl2+endl+
	 	"fax: (949)269-9146"+endl2+endl+
		"info@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam)"+endl2+endl+
		"http://www.picsauditing.com";

	private static Properties getProperties(){
		Properties props = new Properties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.quitwait", "false");
		return props;
	}//getProperties

	private static Properties getPropertiesBackup2(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP2);
		return props;
	}//getPropertiesBackup2
	private static Properties getPropertiesBackup3(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP3);
		return props;
	}//getPropertiesBackup3
	private static Properties getPropertiesBackup4(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP4);
		return props;
	}//getPropertiesBackup4
	private static Properties getPropertiesBackup5(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP5);
		return props;
	}//getPropertiesBackup5
	private static Properties getPropertiesBackup6(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP6);
		return props;
	}//getPropertiesBackup6
/*	private static Properties getPropertiesBackup7(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP7);
		return props;
	}//getPropertiesBackup7
	private static Properties getPropertiesBackup8(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP8);
		return props;
	}//getPropertiesBackup8
	private static Properties getPropertiesBackup9(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP9);
		return props;
	}//getPropertiesBackup9
	private static Properties getPropertiesBackup10(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP10);
		return props;
	}//getPropertiesBackup10
	private static Properties getPropertiesBackup11(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP11);
		return props;
	}//getPropertiesBackup11
	private static Properties getPropertiesBackup12(){
		Properties props = getProperties();
		props.put("mail.smtp.user", EMAIL_ACCOUNT_BACKUP12);
		return props;
	}//getPropertiesBackup12
*/
	private static class SMTPAuthenticator extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticator

	private static class SMTPAuthenticatorBackup2 extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT_BACKUP2,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticatorBackup2
	private static class SMTPAuthenticatorBackup3 extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT_BACKUP3,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticatorBackup3
	private static class SMTPAuthenticatorBackup4 extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT_BACKUP4,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticatorBackup4
	private static class SMTPAuthenticatorBackup5 extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT_BACKUP5,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticatorBackup5
	private static class SMTPAuthenticatorBackup6 extends javax.mail.Authenticator{
	    public PasswordAuthentication getPasswordAuthentication(){ 
      	  return new PasswordAuthentication(EMAIL_ACCOUNT_BACKUP6,PASSWORD);
    	}//getPasswordAuthentication
	}//SMTPAuthenticatorBackup6

	/**
	 * Usage: eBean.init(config);
	 * @param config
	 */
	public static void init(javax.servlet.ServletConfig config) {
		rootPath = config.getServletContext().getRealPath("/");
	}//init
	
	public static void sendEmail(Email email) throws Exception {
		sendEmail(email.getFromAddress(), email.getToAddress(), email.getCcAddress(), email.getSubject(), email.getBody());
	}
	
	private static void sendEmail(String from, String to, String cc, String subject, String text) throws Exception {
		if (IS_TESTING){
			writeTestEmail(from,to,cc,subject,text);
			return;
		}//if
		try{
			Authenticator auth = new SMTPAuthenticator();
			Session s = Session.getInstance(getProperties(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		try{
			Authenticator auth = new SMTPAuthenticatorBackup2();
			Session s = Session.getInstance(getPropertiesBackup2(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());			
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		try{
			Authenticator auth = new SMTPAuthenticatorBackup3();
			Session s = Session.getInstance(getPropertiesBackup3(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());			
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		try{
			Authenticator auth = new SMTPAuthenticatorBackup4();
			Session s = Session.getInstance(getPropertiesBackup4(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());			
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		try{
			Authenticator auth = new SMTPAuthenticatorBackup5();
			Session s = Session.getInstance(getPropertiesBackup5(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());			
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		try{
			Authenticator auth = new SMTPAuthenticatorBackup6();
			Session s = Session.getInstance(getPropertiesBackup6(),auth);
			MimeMessage message = new MimeMessage(s);
			InternetAddress f = new InternetAddress(from);
			message.setFrom(f);
			InternetAddress t = new InternetAddress(to);
			message.addRecipient(Message.RecipientType.TO, t);
			if (Utilities.isValidEmail(cc)){
				InternetAddress c = new InternetAddress(cc);
				message.addRecipient(Message.RecipientType.CC, c);
			}//if
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message, message.getAllRecipients());			
			return;
		}catch (Exception ex){
			System.out.println("Send Mail Exception:"+ex.getMessage());
		}//catch
		System.out.println("Unable to send email:");
		System.out.println("  from:"+from);
		System.out.println("  to:"+to+", cc:"+cc);
		System.out.println("  subject:"+subject);
		System.out.println("  text:"+text);
	}//sendEmail

	private static void sendEmail(String from, String to, String subject, String text) throws Exception {
		sendEmail(from, to, "", subject, text);
	}//sendEmail

		//added for send activation emails to operators to multiple recipients
	public static void sendEmails(String from, String to, String cc, String subject, String text) throws Exception {
		if (IS_TESTING){
			writeTestEmail(from,to,cc,subject,text);
			return;
		}//if
		Authenticator auth = new SMTPAuthenticator();
		Session s = Session.getInstance(getProperties(),auth);
		MimeMessage message = new MimeMessage(s);
		InternetAddress f = new InternetAddress(from);
		message.setFrom(f);
		message.addRecipients(Message.RecipientType.TO, to);	
		if (Utilities.isValidEmail(cc)){
			InternetAddress c = new InternetAddress(cc);
			message.addRecipient(Message.RecipientType.CC, c);
		}		
		message.setSubject(subject);
		message.setText(text);
		
		Transport transport = s.getTransport("smtp");
		transport.connect();
		transport.sendMessage(message, message.getAllRecipients());	
	}//sendEmail

	private static void sendAttachment(String from, String to, String cc, String subject, String text, 
										String path, String fileName) throws Exception {
		if (IS_TESTING){
			writeTestEmail(from,to,"","Attach file:"+fileName+endl2+endl+subject,text);
			return;
		}//if
	
		Authenticator auth = new SMTPAuthenticator();
		Session s = Session.getInstance(getProperties(),auth);
//		s.setDebug(true);
		MimeMessage message = new MimeMessage(s);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		
		Multipart multipart = new MimeMultipart();

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(text);
		multipart.addBodyPart(messageBodyPart);
		
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(path + fileName);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		multipart.addBodyPart(messageBodyPart);
		
		message.setContent(multipart);

		Transport transport = s.getTransport("smtp");
//		transport.connect("mail.picsauditing.com", username, pass);
		transport.connect();
		transport.sendMessage(message, message.getAllRecipients());	
	}//sendAttachment

	private static void writeTestEmail(String from, String to, String cc, String subject, String text) throws Exception {
		java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(EMAIL_OUT,true));
		if (Utilities.isValidEmail(cc))
			out.write("Date/Time:"+DateBean.getTodaysDateTime()+endl2+endl+"From: "+from+endl2+endl+"To: "+to+endl+"CC: "+cc+endl+"Subject: "+subject+endl2+endl+"Text: "+text);
		else
			out.write("Date/Time:"+DateBean.getTodaysDateTime()+endl2+endl+"From: "+from+endl2+endl+"Subject: "+subject+endl2+endl+"Text: "+text);
	    out.write(endl2+endl+"*******************************************************"+endl2+endl+endl2+endl);
		out.close();
	}//writeTestEmail
	
	private static void writeTestEmail(String from, String to, String subject, String text) throws Exception {
		java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(EMAIL_OUT,true));
			out.write("Date/Time:"+DateBean.getTodaysDateTime()+endl2+endl+"From: "+from+endl2+endl+"Subject: "+subject+endl2+endl+"Text: "+text);
	    out.write(endl2+endl+"*******************************************************"+endl2+endl+endl2+endl);
		out.close();
	}//writeTestEmail

	public void sendWelcomeEmail(AccountBean aBean, String adminName) throws Exception {
		//setFromDB();
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(aBean.id);
		String from = FROM_INFO;
		String to = aBean.email;
		String cc = "";
		if (null!=cBean.secondEmail)
		   cc = cBean.secondEmail;
		
		AppPropertiesBean props = new AppPropertiesBean();
		
		String subject = props.get("email_welcome_subject");
		
		String body = props.get("email_welcome_body");
		body = body.replace("${user.name}", aBean.name);
		body = body.replace("${user.username}", aBean.username);
		body = body.replace("${user.password}", aBean.password);
		
		String footer = props.get("email_footer");
		footer = footer.replace("${fax}", props.get("main_fax"));
		footer = footer.replace("${email}", props.get("main_email"));
		footer = footer.replace("${ext}", "");
		body = body.replace("${email_footer}", footer);

		boolean attachfile = props.get("email_welcome_attachfile").equals("1");
		if (attachfile) {
			if (null == rootPath)
				throw new Exception("Email Bean not initialized with path");
			String path = rootPath + "attachments/";
			String fileName = "welcome.doc";
			sendAttachment(from, to, cc, subject, body, path, fileName);
		} else {
			sendEmail(from, to, cc, subject, body);
		}//else
		cBean.writeWelcomeEmailDateToDB(aBean.id, adminName);
	}//sendWelcomeEmail

	public static void sendPasswordEmail(String id, String username, String pass, 
				String email, String contact) throws Exception {
		String from = FROM_INFO;
		String to = email;
		String cc = "";
		
		// If contractor has secondEmail, cc them too
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(id);
		if (null!=cBean.secondEmail)
		   cc = cBean.secondEmail;
		
		AppPropertiesBean props = new AppPropertiesBean();
		String subject = props.get("email_password_subject");
		
		String message = props.get("email_password_body");
		message = message.replace("${username}", username);
		message = message.replace("${password}", pass);
		message = message.replace("${contact}", contact);
		String footer = props.get("email_footer");
		footer = footer.replace("${fax}", props.get("main_fax"));
		footer = footer.replace("${email}", props.get("main_email"));
		footer = footer.replace("${ext}", "");
		message = message.replace("${email_footer}", footer);

		sendEmail(from, to, cc, subject, message);
		
		// If contractor, add a note to their account
		cBean.addNote(id, "(PICS)", "Password email reminder sent to "+contact+" ("+email+")", DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendPasswordEmail

	public static void sendJohnNewAccountEmail(String name) throws Exception {
		String from = FROM_INFO;
		String to = "jcota@picsauditing.com";
		String cc = "";
		String subject = "Jesse, are you having trouble satisfying your woman?";
		String message = "Take advantage of this one time offer.  You'll be amazed at the results. "+
			"Guaranteed to increase your performance, and her pleasure.  Oh yeah, and "+name+" has created an account online. "+
			"PICS operators are standing by. Call our ED expert now, at 949-933-9600.  Ask for Jared.";
		sendEmail(from, to, cc, subject, message);
	}//sendJohnNewAccountEmail

	public static void sendCertificateExpireEmail(String conID, String email, String contact, String type,
					String date, String operator, String adminName) throws Exception {
		String JEFF_POLLOCK_EMAIL_FOOTER =
			"PICS - Pacific Industrial Contractor Screening"+endl2+endl+
			"P.O. Box 51387"+endl2+endl+
			"Irvine CA 92619-1387"+endl2+endl+
			"(949)387-1940"+endl2+endl+
		 	"fax: (949)269-9149"+endl2+endl+
			"eorozco@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam)"+endl2+endl+
			"http://www.picsauditing.com";
		
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(conID);
		String from = FROM_INFO;
		String to = email;
		String cc = "";
		if (Utilities.isValidEmail(cBean.secondEmail))
		   cc = cBean.secondEmail;
		String subject = operator + " insurance certificate about to expire";
		String message = "Attn: "+contact+" ("+aBean.name+")"+endl+endl+
			"This is an automatically generated email from "+operator+
			" to remind you that your company has an insurance "+
			"certificate that has expired or is about to expire."+endl+endl+
			"The "+type+" Certificate of Insurance for "+operator+" expires on "+date+". "+
			"Please mail, email or fax us a new insurance certificate listing "+operator+" as the additional "+ 
			"insured."+endl+endl+
			"If we do not receive this certificate prior to the expiration date you will not"+
			"be permitted to work for us."+endl+endl+
			"As always we appreciate your services and are here to answer any questions you may "+
			"have."+endl+endl+operator+" c/o"+JEFF_POLLOCK_EMAIL_FOOTER;
		sendEmail(from, to, cc, subject, message);
		cBean.addNote(conID, "("+adminName+")", "Expired "+type+" ("+date+") email sent", DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendCertificateExpireEmail

	// 12-7-04 Brittney - Changed text, seperate email for auditor. 
	// 1/6/04 jj - changed method paramets to be references to account and contractor Beans, added contact phone # and address to email
	public static void sendAuditEmail(AccountBean aBean, ContractorBean cBean, boolean toAuditor) throws Exception {
		String con_id = aBean.id;
		String contact = aBean.contact;
		String address = aBean.getFullAddress();
		String phone = aBean.phone;
		String companyName = aBean.name;
		String auditDate = cBean.auditDate;
		String auditTime = cBean.getAuditTime();
		String from = FROM_INFO;
		String to;
		String cc;
		String subject = "Reminder about PICS audit on " + auditDate;
		String message;
		if (toAuditor) {
			 
			UserBean tempABean = new UserBean();
			tempABean.setFromDB(cBean.auditor_id);
			to = tempABean.email;
			cc = "";
			if (null!=cBean.secondEmail)
			   cc = cBean.secondEmail;
			message = "This is an automatically generated email from Pacific Industrial Contractor Screening "+
				" (PICS) to remind you of an audit you have scheduled to perform with "+
				companyName + " on " + auditDate;
			if (!auditTime.equals(""))
				message += " at " + auditTime;
			message += ". Their address is:"+endl+endl+address+endl+endl+
				"Please make sure you log into your account to review the safety qualification "+
				"forms and details about the company in order to assist you in preparing for the audit. "+
				"There is also a map on the contractors details page if you need assistance in driving to the location. "+
				"Additionally, you should be receiving a confirmation email from the companies you are assigned to audit. "+
				"If you do not receive this it is a good idea to call them to confirm yourself. "+
				"The contact person at "+companyName+" is "+contact+" and can be reached at "+phone+". "+
				"Nobody else at PICS will be calling the companies assigned to you. Therefore, it is your responsibility to "+
				"remember to confirm all appointments. Every effort has been made to make sure that the contractors are "+
				"prepared for the audit so that your time is manageable."+
				endl+endl+
				"Thank you and please click on this link to confirm receipt of this notice:"+
				endl+endl+
				"http://www.picsauditing.com/audit_confirmEmailReceipt.jsp?id="+con_id+"&isaud=true"+
				endl+endl+
				"If you have any questions or concerns about the audit, contact the audit manager, Jesse Cota, "+
				"@ 949-387-1940, ext. 704 or email him at jcota@picsauditing.com."+
				endl+endl+
				"We appreciate your time and effort in making the process a pleasant experience for our customers!"+
				endl+endl+
				"Thanks,"+
				endl+endl+
				"John Moreland"+
				endl+endl+EMAIL_FOOTER; 
		} else {
			//Contractor email
			to = aBean.email;
			cc = "";
			if (null!=cBean.secondEmail)
			   cc = cBean.secondEmail;
			message = "Attn: "+aBean.name+endl+endl+
				"This is an automatically generated email from Pacific Industrial Contractor Screening "+
				"(PICS) to remind you that your company has a safety audit scheduled for "+
				auditDate;
			if (!auditTime.equals(""))
				message+=" at "+auditTime;
			message+="."+endl+endl+
				"Prior to the audit date please review the 'how to prepare for the onsite audit' "+
				"and 'onsite audit form' in the forms and documents section of our website under PICS. "+
				"You will need to log in with your username and password. It is important that you review these materials "+
				"as we will be reviewing documentation to include inspection forms, training records, safety meeting sheets, etc."+
				endl+endl+
				"If you have any questions, please contact us so that we can help you prepare for the "+
				"audit. The more prepared you are, the quicker the process will move."+
				endl+endl+
				"Additionally, please note that there is a $150 cancellation fee if you do not give us at least a "+
				"48-hour notice."+
				endl+endl+
				"To finalize the scheduling of your audit, click on the link below to confirm receipt of this notice."+
				endl+endl+
				"http://www.picsauditing.com/audit_confirmEmailReceipt.jsp?id="+con_id+"&isaud=false"+
				endl+endl+EMAIL_FOOTER;					
		} //else
		sendEmail(from, to, cc, subject, message);
	}//sendAuditEmail

	public static void sendConfirmationEmail(String conID, boolean isToAuditor) throws Exception {
	// 6/27/05 jj - updated sendConfirmationEmail() to write to notes
	// 1/7/05 jj - updated sendConfirmationEmail() to include audit date and time
		AccountBean aBean = new AccountBean();
		ContractorBean cBean = new ContractorBean();
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		String companyName = aBean.name;
		String contact;
		if (isToAuditor) {
			UserBean tempABean = new UserBean();
			tempABean.setFromDB(cBean.auditor_id);
			contact = tempABean.name+"(auditor)";
		}//if
		else
			contact = aBean.contact;
		String to = "jcota@picsauditing.com";
		String cc = "";
		String from = FROM_INFO;
		String subject = "Email Received by "+contact+" Re: Audit for "+companyName;
		String message = contact+" confirmed receipt of email regarding "+companyName+" audit on "+
			cBean.auditDate+" at "+cBean.getAuditTime()+"."+
			endl+endl+endl+endl+EMAIL_FOOTER;
		sendEmail(from, to, cc, subject, message);
		cBean.addNote(conID, "(PICS)", "Audit email received by "+contact+" for "+companyName, DateBean.getTodaysDateTime());
		cBean.writeToDB();
//		to = "msandwall@picsauditing.com";	
//		sendEmail(from, to, subject, message);
		//to = "brittney_jensen@hotmail.com";			
		//sendEmail(from, to, subject, message);
	}//sendConfirmationEmail

	public static void sendAuditClosedEmails(String conID, String adminName, String auditType) throws Exception {
		AccountBean aBean = new AccountBean();
		OperatorBean oBean = new OperatorBean();
		aBean.setFromDB(conID);
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		String to = aBean.email;		
		String cc = "";
		if (null!=cBean.secondEmail)
			   cc = cBean.secondEmail;
		String from = FROM_INFO;
		String subject = "Change in Audit Status";
		String message =  "Hi "+aBean.contact+","+endl+endl+
			"Thank you for your time and effort in closing out your company's "+auditType+" Audit requirements. "+
			"Often these audits can be an interruption in your daily operations and we can appreciate "+
			"the time in focusing on the safety of your company and employees. "+
			endl+endl+
			"You will be notified each year to update your online qualification form and we look forward to seeing you again in three years. "+
			"Until then, we hope that your company is prosperous and your safety culture continues to improve. "+
			"If you have any questions or are in need of anything further, feel free to contact us anytime."+
			endl+endl+"Thank you,"+endl+endl+EMAIL_FOOTER;
		sendEmail(from, to, cc, subject, message);
		cBean.addNote(conID, "("+adminName+")", auditType+"Audit closed email sent to: "+to, DateBean.getTodaysDateTime());
		// sends emails to those operators wishing to be notified that this contractor was activated 
		String contractorName = aBean.name;
		String[] generals = cBean.getGeneralContractorsArray();
		for (int x = 0; x < generals.length; x++) {
			aBean.setFromDB(generals[x]);
			oBean.setFromDB(generals[x]);
			to = oBean.activationEmails;
			String operatorName = aBean.name;
			if ("Yes".equals(oBean.doSendActivationEmail) && (null != to)) {
				if ((com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && oBean.canSeeOffice()) ||
						(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && oBean.canSeeDesktop() && !oBean.canSeeOffice())) {
					sendAuditClosedEmailToOperator(conID,to,cc,contractorName,operatorName,auditType);
					cBean.addNote(conID, "(PICS)", auditType+" Audit closed emails sent to "+operatorName+" ("+to+")", DateBean.getTodaysDateTime());
				}//if
			}//if
		}//generals for
		cBean.writeToDB();
	}//sendAuditClosedEmails
	
	public static void sendAuditClosedEmailToOperator(String conID, String to, String cc, String contractorName,
						String operatorName, String auditType) throws Exception {
// 6/27/05 jj - updated to write to notes
// 7/11/05 jj - changed 'active' verbiage to 'closed requirements'
		if (null==cc)
		   cc = "";
		String from = FROM_INFO;
		String subject = contractorName+" has closed all of its audit requirements";
		String message = "Hello "+operatorName+","+endl+endl+
			contractorName+" has completed its "+auditType+" Audit and closed out any requirements. "+ 
			"However, this does not necessarily mean that this contractor is Active because they may have "+
			"other audits outstanding."+endl+
			"For complete details on this contractor please go to our website."+
			endl+endl+EMAIL_FOOTER;
		sendEmails(from, to, cc, subject, message);
	}//sendAuditClosedEmailToOperator

	public static void sendContactUsEmail(String name, String email, String phone, String sendTo, String message) throws Exception {
		String text = "Name: "+name+"\nEmail: "+email+"\nPhone: "+phone+"\nMessage:\n"+message;
		sendEmail(email,sendTo,"","Email from PICS website",text);
	}//sendContactUsEmail

	public static void sendAuditSurveyEmail(String conID) throws Exception {
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(conID);		
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		UserBean tempABean = new UserBean();
		tempABean.setFromDB(cBean.auditor_id);
		String auditor = tempABean.name;
		String to = aBean.email;
		String cc = "";
		if (null!=cBean.secondEmail)
		   cc = cBean.secondEmail;
		String from = FROM_INFO;
		String subject = "PICS Office Audit Follow Up";
		String message =  "Hello "+aBean.contact+","+endl+endl+
			"We want to hear from you!"+endl+endl+
			"We hope that your visit today with our PICS auditor, "+auditor+", was beneficial for you. We try to help "+
			"contractors like "+aBean.name+" to prequalify to work in the facilities that request PICS' service. "+
			"My name is Jared Smith, and I represent PICS' effort to ensure that you have a good experience working with "+
			"our employees and website. We would appreciate it if you could take a few minutes and provide us with "+
			"feedback regarding the visit today and the overall prequalification process."+endl+endl+
			"Please be honest with us as we want to create a better experience for you in the future and "+
			"for others. Don't worry that your feedback, whether negative or positive, will affect your requirements "+
			"needed or status with PICS. Here is the link to the survey:"+endl+endl+
			"<a href=http://www.surveymonkey.com/s.asp?u=929543410015>Take PICS feedback survey</a>"+endl+endl+
			"If the link does not work, please copy and paste the following line into your browser:"+endl+
			"http://www.surveymonkey.com/s.asp?u=929543410015"+endl+endl+
			"If you had any requirements to fulfill for your audit, you can log into your account at www.picsauditing.com "+
			"and click on 'View Audit Requirements'.  This will indicate the current status of your pending requirements." +endl+endl+
			"Also, we hope you were able to see the 'My Details' page where you can write some marketing paragraphs about your "+
			"company's services. Your current and prospective clients will read what services your company provides. "+
			"If you have any questions about what to include or what your clients and prospective clients see when they look at your "+
			"'My Details' page, let me know."+endl+endl+
			"Finally, we should mention that if you are asked by any other buyers (like other refineries, pipeline groups, power plants, "+
			"etc) to go through any kind of prequalification, we have a letter that you can send to them requesting that we provide our "+
			"PICS audit to them. It is on the 'Forms & Documents' page that you get by clicking on the 'Forms & Docs' link. "+
			"Then click on the Form entitled 'Operator Referral Form Letter'.  "+
			"You can also give them my contact info:"+endl+endl+
			"Jared Smith"+endl+
			"Email: jsmith@picsauditing.com"+endl+
			"Phone: (949)387-1940 x706"+endl+
			"Fax: (949)480-2029"+endl+endl+
			"Warm Regards,"+endl+
			"Jared";
		sendEmail(from, to, cc, subject, message);
		cBean.addAdminNote(conID, "(PICS)", "Office Audit survey email sent to: "+to, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendAuditSurveyEmail

	public static void sendDesktopClosedSurveyEmail(String conID) throws Exception {
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(conID);		
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		String to = aBean.email;
		String cc = "";
		if (null!=cBean.secondEmail)
		   cc = cBean.secondEmail;
		String from = FROM_INFO;
		String subject = "PICS Desktop Audit Follow Up";
		String message =  "Hello "+aBean.contact+","+endl+endl+
			"We want to hear from you!"+endl+endl+
			"Congratulations on closing out the Desktop Audit today! We try to help "+
			"contractors like "+aBean.name+" to prequalify to work in the facilities that request PICS' service. "+
			"My name is Jared Smith, and I represent PICS' effort to ensure that you have a good experience working with "+
			"our employees and website. We would appreciate it if you could take a few minutes and provide us with "+
			"feedback regarding both the PQF on the website and the Desktop Audit."+endl+endl+
			"Please be honest with us as we want to create a better experience for you in the future and "+
			"for others. Don't worry that your feedback, whether negative or positive, will affect your requirements "+
			"needed or status with PICS. Here is the link to the survey:"+endl+endl+
			"<a href=http://www.surveymonkey.com/s.asp?u=300363409969>Take PICS feedback survey</a>"+endl+endl+
			"If the link does not work, please copy and paste the following line into your browser:"+endl+
			"http://www.surveymonkey.com/s.asp?u=300363409969"+endl+endl+
			"If you would like to check your status at any given facility, you can log into your account at "+
			"www.picsauditing.com and click on the 'Facilities' link.  This will indicate your status for "+
			"all the facilities where you work.  Some facilities also require the Office Audit, which may be why "+
			"you are still Inactive. If you are Active, the facility does not require the Office Audit."+endl+endl+
			"Also, we hope you were able to see the 'My Details' page where you can write some marketing paragraphs about your "+
			"company's services. Your current and prospective clients will read what services your company provides. "+
			"If you have any questions about what to include or what your clients and prospective clients see when they look at your "+
			"'My Details' page, let me know."+endl+endl+
			"Finally, we should mention that if you are asked by any other buyers (like other refineries, pipeline groups, power plants, "+
			"etc) to go through any kind of prequalification, we have a letter that you can send to them requesting that we provide our "+
			"PICS audit to them. It is on the 'Forms & Documents' page that you get by clicking on the 'Forms & Docs' link. "+
			"Then click on the Form entitled 'Operator Referral Form Letter'.  "+
			"You can also give them my contact info:"+endl+endl+
			"Jared Smith"+endl+
			"Email: jsmith@picsauditing.com"+endl+
			"Phone: (949)387-1940 x706"+endl+
			"Fax: (949)480-2029"+endl+endl+
			"Warm Regards,"+endl+
			"Jared";
		sendEmail(from, to, cc, subject, message);
		cBean.addAdminNote(conID, "(PICS)", "Desktop Audit survey email sent to: "+to, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendDesktopClosedSurveyEmail

	public static Email startContractorEmail(String conID, String propertyName) throws Exception {
		Email email = new Email();
		email.setFromAddress(FROM_INFO);
		
		email.setToAddress(email.getAccount(conID).email);
		email.addTokens("aBean.name", email.getAccount().name);
		email.addTokens("aBean.contact", email.getAccount().contact);
		email.useDefaultFooter();
		email.setEmailTypeProperty(propertyName);
		
		return email;
	}
	
	public static void sendAnnualUpdateEmail(String conID, String adminName) throws Exception {
		Email email = startContractorEmail(conID, "annual_update");
		email.setCcAddress(email.getContractor(conID).secondEmail);
		
		sendEmail(email);

		email.getContractor().addNote(conID, "("+adminName+")", "Annual update email sent to: "+email.getToAddress(), DateBean.getTodaysDateTime());
		email.getContractor().lastAnnualUpdateEmailDate=DateBean.getTodaysDate();
		email.getContractor().annualUpdateEmails++;
		email.getContractor().writeToDB();
	}
	
	public static void sendDesktopSubmittedEmail(String conID, String adminName) throws Exception {
		Email email = startContractorEmail(conID, "desktopsubmit");
		email.setCcAddress(email.getContractor(conID).getAuditorsEmail());
		
		sendEmail(email);
		
		email.getContractor().addNote(conID, "("+adminName+")", "Desktop submitted email sent to: "+email.getToAddress(), DateBean.getTodaysDateTime());
		email.getContractor().writeToDB();
	}

	public static void sendDaSubmittedEmail(String conID, String adminName) throws Exception {
		Email email = startContractorEmail(conID, "dasubmit");
		email.setCcAddress(email.getContractor(conID).getAuditorsEmail());
		
		sendEmail(email);
		
		email.getContractor().addNote(conID, "("+adminName+")", "D&A submitted email sent to: "+email.getToAddress(), DateBean.getTodaysDateTime());
		email.getContractor().writeToDB();
	}
	
	public static void sendContractorAddedEmail(String conID, String opName, String userName) throws Exception {
		Email email = startContractorEmail(conID, null);
		email.setCcAddress(email.getContractor(conID).secondEmail);
		
		email.addTokens("username", userName);
		email.addTokens("opName", opName);
		email.setEmailTypeProperty("contractoradded");
		
		sendEmail(email);
		
		email.getContractor().addNote(conID, "", userName+" from "+opName+" added "+email.getAccount().name+" to db, email sent to: "+email.getToAddress(), DateBean.getTodaysDateTime());
		email.getContractor().writeToDB();
	}

	public static void sendUpdateDynamicPQFEmail(String conID) throws Exception {
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(conID);
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		String to = aBean.email;
		String cc = "";
		if (null!=cBean.secondEmail)
		   cc = cBean.secondEmail;
		String from = FROM_INFO;
		String subject = "A facility has added you to their PICS database";
		String message =  "Hello "+aBean.contact+","+endl+endl+
			"This is an automatically generated email to inform you that your company has been added to a facility's PICS database. "+
			"For this reason, there may be new sections for you to fill out in your PQF. "+
			"Please log in, review your PQF, fill out any additional sections, and re-submit your pqf.  This will ensure that "+
			"you meet the requirements for all your facilities and remain on their active contractors list."+
			endl+endl+
			"Thanks, and have a safe year!"+endl+endl+EMAIL_FOOTER;
		sendEmail(from, to, cc, subject, message);
		cBean.addNote(conID, "", "Update pqf email sent to: "+to, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendUpdateDynamicPQFEmail
	
	public static void sendCertificateRejectedEmail(CertificateDO certDO,Permissions permissions) throws Exception {
		AccountBean aBean = new AccountBean();		
		String conID = certDO.getContractor_id();
		aBean.setFromDB(conID);
		String contactName = aBean.contact;
		String contractor = aBean.name;
		String email = aBean.email;
		
		aBean.setFromDB(certDO.getOperator_id());
		String operator = aBean.name;
		String operatorName = aBean.contact;
		
		User user = new User();
		user.setFromDB(permissions.getUserIdString());
		String reason = certDO.getReason();
		String certType = certDO.getType();
		String from = FROM_INFO;
		String to = email;
		String subject = operator + " insurance certificate rejected";
		String message = "Hello " + contactName + ","+endl+endl+
		contractor + "'s " + certType + endl + 
		"Insurance Certificate has been rejected by "+ endl+
		operatorName + " from " +operator + endl +
		"for the following reasons:" + endl+ endl+
		reason + endl+endl+	
		"Please correct these issues and re-upload your insurance certificate to your " +endl+
		"PICS account." + endl +
	    "If you have any specific questions about " + operator + "'s insurance requirements, " + endl +
	    "please contact " + permissions.getName() + " at "+user.userDO.email+"."+endl+endl+
		"Have a great day,"+endl+
		"PICS Customer Service";
		
		//System.out.println("From:" + from);
		//System.out.println("To:" + to);
		//System.out.println("Subject:" + subject);
		//System.out.println("Message:" + message);
		sendEmail(from, to, subject, message);	
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		cBean.addNote(conID, "("+permissions.getName()+")", certType+" Insurance Certificate rejected by "+operator+" for reason: "+reason, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendCertificateRejectedEmail
	
	public static void sendCertificateAcceptedEmail(CertificateDO certDO, Permissions permissions) throws Exception {
		AccountBean aBean = new AccountBean();		
		String conID = certDO.getContractor_id();
		aBean.setFromDB(conID);
		String contactName = aBean.contact;
		String contractor = aBean.name;
		String email = aBean.email;
		
		aBean.setFromDB(certDO.getOperator_id());
		String operator = aBean.name;
		String operatorName = aBean.contact;
				
		String certType = certDO.getType();
		String from = FROM_INFO;
		String to = email;
		
		String subject = operator + " insurance certificate accepted";
		String message = "Hello " + contactName + ","+endl+endl+
		operatorName + " from " + operator + endl +
		"has approved " + contractor + "'s " + endl + 
		certType + " insurance requirements " + endl +
		"to work at " + operator + "." + endl+endl+
		"Please make sure that you keep up-to-date in PICS by uploading your "+endl+
		"insurance certificate when you renew your policy." + endl+endl+		 
		"Have a great day,"+endl+
		"PICS Customer Service";
		
		sendEmail(from, to, subject, message);
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		cBean.addNote(conID, "("+permissions.getName()+")", certType+" Insurance Certificate accepted by "+operator, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//sendCertificateAcceptedEmail
	
	public static void sendSafetyMeetingEmail(HttpServletRequest request) throws Exception {
		String JESSE_EMAIL_FOOTER =
			"PICS - Pacific Industrial Contractor Screening"+endl2+endl+
			"P.O. Box 51387"+endl2+endl+
			"Irvine CA 92619-1387"+endl2+endl+
			"(949)387-1940"+endl2+endl+
		 	"fax: (949)269-9177"+endl2+endl+
			"info@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam)"+endl2+endl+
			"http://www.picsauditing.com";
	
		
		String to = "meetings@picsauditing.com";
		String cc = "";
		String from = request.getParameter("email");
		String subject = request.getParameter("name") + " from " + request.getParameter("organization") + " registered";
		
		StringBuffer message = new StringBuffer();
		message.append("Info:\n");
		message.append("Name:          " + request.getParameter("name") + "\n");
		message.append("Organization:  " +request.getParameter("organization") + "\n");
		message.append("Phone:         " +request.getParameter("phone") + " " + request.getParameter("ext") + "\n");
		message.append("Email:         " +request.getParameter("email") + "\n");
		message.append("Number:        " +request.getParameter("howmany") + "\n");
		message.append("Attendees:     " +request.getParameter("attendees") + "\n");
		message.append("Special Needs: " +request.getParameter("specialneeds") + "\n");
		
		sendEmail(from, to, cc, subject, message.toString());
		
		to = request.getParameter("email");
		from = "meetings@picsauditing.com";
		subject = "Registration confirmation for Contractor User Group Meeting";
		message = new StringBuffer();
		
		message.append("Hi, " + request.getParameter("name") + "\n\n\n");
		message.append("Thanks for registering for PICS Contractor User Group Meeting 2008.\nIf you have any questions about your registration, please contact Whitney Curry\nat (949)387-1940 x 714 or wcurry@picsauditing.com.");
		message.append("\n\nHave a great day,\nJesse Cota\n\n");
		message.append(JESSE_EMAIL_FOOTER);
		
		sendEmail(from, to, cc, subject, message.toString());
	}//sendConfirmationEmail
	
	public void sendNewUserEmail(HttpServletRequest request, String accountID, String name,
				String username, String pass, String email) throws Exception {
		String from = FROM_INFO;
		String to = email;
		String cc = "";
		String subject = "New PICS User Account Created";
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(accountID);
		String message = "Hi "+name+","+endl+endl+
			"At the request of "+aBean.contact+", you have been issued login info for "+
			"the "+aBean.name+" account with PICS."+endl+endl+
			"Your username: '"+username+"'"+endl+
			"Your password: '"+pass+"'"+endl+endl+
			"Attached is a training manual in case you have any questions."+endl+endl+
			"Have a great week,"+endl+
			"PICS Customer Service"+endl+endl+
			EMAIL_FOOTER;

		String ftpDir = request.getSession().getServletContext().getInitParameter("FTP_DIR");
		String path = ftpDir+"/attachments/";
		String fileName = "userManual.pdf";

		java.io.File userManual = new java.io.File(path+fileName);
		if (userManual.exists())
			sendAttachment(from, to, cc, subject, message, path, fileName);
		else
			sendEmail(from, to, subject, message);			
	}//sendWelcomeEmail
	
	public static void sendErrorMessage(String errorMessage) throws Exception {
		sendEmail("errors@picsauditing.com", "errors@picsauditing.com", "PICS Exception Error", errorMessage);
	}
	
}//EmailBean