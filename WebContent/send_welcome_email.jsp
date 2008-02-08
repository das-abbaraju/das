<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="eBean" class="com.picsauditing.PICS.EmailBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%
	String id = request.getParameter("i");
	if ((null == id) || ("".equals(id)))
		throw new Exception("No contractor_id specified.");

	aBean.setFromDB(id);
	if ((null == aBean.email) || ("".equals(aBean.email)))
		throw new Exception("No email address specified.");

	eBean.init(config);
	eBean.sendWelcomeEmail(aBean, permissions.getUsername());
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<center>A welcome email has been sent to: <b><%=aBean.email%></b><br>
	<br>
	username: <b><%=aBean.username%></b><br>
	password: <b><%=aBean.password%></b><br><br>
	<a href="javascript:window.close();" class="blueMain">Close This Window</a>
	</center>
</body>
</html>
