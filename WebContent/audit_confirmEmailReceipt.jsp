<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%
	String con_id = request.getParameter("id");
	boolean isToAuditor = "true".equals(request.getParameter("isaud"));
	com.picsauditing.PICS.EmailBean.sendConfirmationEmail(con_id, isToAuditor);
%>
<html>
<head>
<title>Email</title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="blueHeader">
		Thank you for your confirmation.</span></td>
	</tr>
</table>
</body>
</html>
