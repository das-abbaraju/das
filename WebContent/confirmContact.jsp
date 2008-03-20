<%@ page language="java"
	import="java.util.ArrayList,com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean"
	scope="page" />
<%
	String sendTo = request.getParameter("sendTo");
	ArrayList<String> toAddresses = new ArrayList<String>();
	if (sendTo.equals("sales"))
		toAddresses.add("jmoreland@picsauditing.com");
	if (sendTo.equals("sales"))
		toAddresses.add("jsmith@picsauditing.com");
	if (sendTo.equals("billing"))
		toAddresses.add("jsmith@picsauditing.com");
	if (sendTo.equals("billing"))
		toAddresses.add("gjepsen@picsauditing.com");
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

	String name = request.getParameter("name");
	String email = request.getParameter("email");
	String phone = request.getParameter("phone");
	String message = "Contact about " + sendTo + ". Sent to:\n";
	for (String toAddress : toAddresses)
		message = message + toAddress + "\n";
	message = message + "\nCompany: " + request.getParameter("company")
			+ '\n' + request.getParameter("message");
	//	eBean.sendContactUsEmail(name,email,sendTo,message);
	for (String toAddress : toAddresses)
		EmailBean.sendContactUsEmail(name, email, phone, toAddress,
				message);
%>
<html>
<head>
<title>Confirm Contact</title>
<meta name="color" content="#CC6600" />
<meta name="flashName" content="CONTACT" />
<meta name="iconName" content="contact" />
</head>
<body>
<div style="text-align: center">
	<p class="blueHeader">Thank you <%=name%>,</p>
	<p class="blueMain">We will contact you shortly.</p>
	<a href="contact.jsp" class="blueMain">Return to Contact Page</a>
</div>
</body>
</html>
