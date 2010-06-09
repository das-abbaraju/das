<%@ page isErrorPage="true" language="java"
	import="java.util.*,java.io.*,com.opensymphony.xwork2.ActionContext"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.opensymphony.xwork2.ActionContext"%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%@page import="com.picsauditing.mail.SendMail"%>
<%@page import="java.sql.SQLException"%>
<%
	// Parameters
	int priority = Integer.parseInt(request.getParameter("priority"));
	String message = request.getParameter("user_message");
	String to_address = request.getParameter("to_address");
	String from_address = request.getParameter("from_address");
	String user_name = request.getParameter("user_name");
	int exceptionID = Integer.parseInt(request.getParameter("exceptionID"));
	String exception_message = request.getParameter("exception_message");
	String category = request.getParameter("category");
	
	StringBuilder email = new StringBuilder();
	email.append("A user has reported an error on PICS\n");
	email.append("\nRemoteAddr: " + request.getRemoteAddr());
	if (permissions.isLoggedIn()) {
		email.append("\nName: " + permissions.getName());
		email.append("\nUsername: " + permissions.getUsername());
		email.append("\nAccountID: " + permissions.getAccountId());
		if (permissions.getAdminID() > 0)
			email.append("\nAdmin: " + permissions.getAdminID());
		email.append("\nType: " + permissions.getAccountType());
	} else {
		email.append("\nName: " + user_name);
		email.append("\nThe current user was NOT logged in.");
	}
	
	email.append("\n\nError Type: ");
	email.append(category);
	
	if(message != null && (!message.equals("") || !message.equals("undefined"))){
		email.append("\n\nUser Message (Priority "+priority+"): \n");
		email.append(message);
		email.append("\n");
	}

	if(exception_message != null && (!exception_message.equals("") || exception_message.equals("undefined"))){
		email.append("\nError Message:\n");
		email.append(exception_message);
		email.append("\n");
	}
	email.append("\nHeaders:");

	for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
		String headerName = (String) e.nextElement();
		email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
	}
	EmailQueue mail = new EmailQueue();
	mail.setSubject("PICS Exception Error");
	mail.setBody(email.toString());
	mail.setToAddresses(to_address);
	if(permissions.isLoggedIn()){
		mail.setFromAddress(permissions.getEmail());
		mail.setBccAddresses(from_address);
	} else
		mail.setFromAddress(from_address);
	mail.setPriority(priority*10+50);
	
	try {
		EmailSender.send(mail);
	} catch (Exception e) {
		System.out.println("PICS Exception Handler ... sending email via sendMail");
		SendMail sendMail = new SendMail();
		mail.setFromAddress("\"PICS Exception Handler\"<info@picsauditing.com>");
		sendMail.send(mail);
		System.out.println(mail.getBody());
	}
%>