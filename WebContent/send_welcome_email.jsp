<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<%@page import="com.picsauditing.mail.EmailTemplates"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%
String id = request.getParameter("i");
if ((null == id) || ("".equals(id)))
	throw new Exception("No contractor_id specified.");

EmailContractorBean mailer = (EmailContractorBean)SpringUtils.getBean("EmailContractorBean");
mailer.setPermissions(permissions);
mailer.sendMessage(EmailTemplates.welcome, new Integer(id));

String message = "A welcome email was sent to "+mailer.getSentTo();

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
