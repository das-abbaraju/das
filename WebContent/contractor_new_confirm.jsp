<%@ page language="java" errorPage="exception_handler.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<%
	// TODO: don't show the username and password for any account we simply pass in here
	// This is a MAJOR security hole
	String cID = request.getParameter("i");
	aBean.setFromDB(cID);
	if (aBean.lastLogin != null && !aBean.lastLogin.equals("1/1/01"))
		throw new Exception("You can't access this page!");
%>
<html>
<head>
<title>Registration Confirmation</title>
<meta name="color" content="#669966" />
<meta name="flashName" content="REGISTER" />
<meta name="iconName" content="register" />
</head>
<body>
<br><br>
<table width="400" cellpadding="0" cellspacing="0">
	<tr>
		<td class="blueMain">
		<p>Congratulations, your account has been created! <br>
		<span class="redMain">PICS will review your submission and send
		you an invoice within 48 hours. This invoice must be paid before your
		account will be activated</span><br>
		<br>
		You must confirm the email address that you provided us before your
		account will be activated. If you have a spam filter, we suggest you
		add picsauditing.com to the safe sender domain list. Otherwise you may
		not receive our automatically generated emails. After you receive your email
		confirmation, you will be able to log in to our website using the
		following:<br>
		<br>
		Username: <b><%=aBean.username%></b><br>
		Password: <b><%=aBean.password%></b></p>
		</td>
	</tr>
</table>
</body>
</html>
