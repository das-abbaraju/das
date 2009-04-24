<%@ page language="java"
	import="java.util.ArrayList,com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%
	String emailAddress = request.getParameter("email");
	if (Utilities.isValidEmail(emailAddress)) {
		String sendTo = request.getParameter("sendTo");
		ArrayList<String> toAddresses = new ArrayList<String>();
		if (sendTo.equals("sales"))
			toAddresses.add("jmoreland@picsauditing.com");
		if (sendTo.equals("sales"))
			toAddresses.add("jsmith@picsauditing.com");
		if (sendTo.equals("billing"))
			toAddresses.add("billing@picsauditing.com");
		if (sendTo.equals("audits"))
			toAddresses.add("jcota@picsauditing.com");
		if (sendTo.equals("general"))
			toAddresses.add("jfazeli@picsauditing.com");
		if (sendTo.equals("tech"))
			toAddresses.add("jfazeli@picsauditing.com");
		if (sendTo.equals("tech"))
			toAddresses.add("tallred@picsauditing.com");
		if (sendTo.equals("careers"))
			toAddresses.add("careers@picsauditing.com");

		String body = "";
		body += "Name: " + request.getParameter("name");
		body += "\nCompany: " + request.getParameter("company");
		body += "\nEmail: " + emailAddress;
		body += "\nPhone: " + request.getParameter("phone");
		body += "\nMessage:\nContact about " + sendTo + ". Sent to:\n";

		for (String toAddress : toAddresses)
			body += toAddress + "\n";
		body += "\n" + request.getParameter("message");

		EmailQueue mail = new EmailQueue();
		mail.setSubject("Email from PICS website");
		mail.setBody(body);
		mail.setToAddresses(Strings.implode(toAddresses, ","));
		mail.setFromAddress(request.getParameter("email"));

		EmailSender.send(mail);
	}
%>

<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<%@page import="com.picsauditing.util.Strings"%><html>
<head>
<title>Confirm Contact</title>
<meta name="color" content="#CC6600" />
<meta name="flashName" content="CONTACT" />
<meta name="iconName" content="contact" />
</head>
<body>
<div style="text-align: center">
<% if(!Utilities.isValidEmail(emailAddress)) { %>
	<p class="blueHeader">Please Enter a Valid Email Address</p>
<% } else {%>
	<p class="blueHeader">Thank you,</p>
	<p class="blueMain">We will contact you shortly.</p>
<% } %>
<a href="contact.jsp" class="blueMain">Return to Contact Page</a></div>
</body>
</html>
