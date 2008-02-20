<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="eBean" class="com.picsauditing.mail.EmailContractorBean" scope ="page"/>
<%@page import="com.picsauditing.mail.*"%>
<%
String id = request.getParameter("i");
if ((null == id) || ("".equals(id)))
	throw new Exception("No contractor_id specified.");

//eBean.sendWelcome(id, permissions);

EmailContractorBean emailer = new EmailContractorBean();
emailer.sendMessage(EmailTemplates.welcome, id, permissions);
String message = "A welcome email was sent to "+emailer.getSentTo();
emailer.getContractorBean().welcomeEmailDate = DateBean.getTodaysDate();
emailer.getContractorBean().writeToDB();

%>
<html>
<head>
<title>PICS - Welcome Email</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<center><%=message%><br>
	<br>
	<a href="javascript:window.close();" class="blueMain">Close This Window</a>
	</center>
</body>
</html>
