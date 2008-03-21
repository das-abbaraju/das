<%@ page language="java"
	import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp"%>
<%
	String auditType = request.getParameter("auditType");
	String from = request.getParameter("from");
	if (null == from)
		from = "";
	if (null != auditType) {
		session.setAttribute("auditType", auditType);
		if (null != from && !"".equals(from)) {
			response.sendRedirect(from);
			return;
		}
	}
%>
<html>
<head>
<title>Audit Management</title>
</head>
<body>
<table border="0" cellpadding="0" cellspacing="0">
	<tr align="center" class="blueMain">
		<td class="blueMain"><%@ include
			file="includes/nav/editPQFNav.jsp"%></td>
	</tr>
	<tr>
		<td align="center" class="redMain"><br>
		Please select an Audit Type<br>
		<form name="form1" method="post" action="audit_selectType.jsp">
		<%=Inputs.inputSelectFirstSubmit("auditType", "forms", auditType, Constants.AUDIT_TYPE_ARRAY,
							Constants.DEFAULT_AUDIT)%>
		<input type=hidden name=from value="<%=from%>"></form>
		</td>
	</tr>
</table>
</body>
</html>
