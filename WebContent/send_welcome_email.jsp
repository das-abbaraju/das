<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="eBean" class="com.picsauditing.mail.EmailContractorBean" scope ="page"/>
<%
String id = request.getParameter("i");
if ((null == id) || ("".equals(id)))
	throw new Exception("No contractor_id specified.");

eBean.sendWelcome(id, permissions);
%>
<html>
<head>
<title>PICS - Welcome Email</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<center>A welcome email has been sent to: <b><%=eBean.getSentTo()%></b><br>
	<br>
	<a href="javascript:window.close();" class="blueMain">Close This Window</a>
	</center>
</body>
</html>
