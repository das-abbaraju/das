<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean"
	scope="page" />
<%
	permissions.tryPermission(OpPerms.ManageAudits);
	try {
		String auditType = request.getParameter("auditType");
		boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
		boolean isFieldAudit = "Field".equals(auditType);
		String reqText = isFieldAudit ? "Comment" : "Requirement";
		String orderby = request.getParameter("orderby");
		String showReq = request.getParameter("showReq");
		if (showReq == null)
			showReq = "";
%>
<html>
<head>
<title>Audit Preview</title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
<table width="657" height="100%" border="0" cellpadding="0"
	cellspacing="0">
	<tr align="center">
		<td class="blueHeader">PICS Safety Audit<br>
		</td>
	<tr align="center">
		<td><%@ include file="includes/nav/editAuditNav.jsp"%><br>
		<form name="form1" action="audit_preview.jsp" method="get"><%=aqBean.getAuditTypeSelectSubmit("auditType", "forms", auditType)%><br>
		</form>
		</td>
	</tr>
	<%
		if (isAuditTypeSelected) {
	%>
	<tr>
		<td class="blueMain" align="center">
		<%
			if (showReq.equals("true")) {
		%> <a
			href="?showReq=false&auditType=<%=auditType%>" class="redmain">Hide
		<%=reqText%>s</a> <%
 	} else {
 %> <a
			href="?showReq=true&auditType=<%=auditType%>" class="redmain">Show
		<%=reqText%>s</a> <%
 	}//if
 %> <br>
		<br>
		</td>
	</tr>
	<tr align="center">
		<td>
		<table width="657" border="1" bordercolor="#003366" cellpadding="1"
			cellspacing="0">
			<tr class="active">
				<td><font color="#000000"><strong>#</strong></font></td>
				<td><font color="#000000"><strong>Question</strong></font></td>
				<td><font color="#000000"><strong>YES</strong></font></td>
				<td><font color="#000000"><strong>NO</strong></font></td>
				<td><font color="#000000"><strong>NA</strong></font></td>
			</tr>
			<%
				aqBean.setList(orderby, auditType);
				while (aqBean.isNextRecord()) {
			%>
			<tr class="blueMain" <%=aqBean.getBGColor()%>>
				<td valign="top"><%=aqBean.num%></td>
				<td>(<%=aqBean.getCategoryName()%>) <%=aqBean.question%> <%=aqBean.getLinksShow()%></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<%
					if (showReq.equals("true")) {
						if (!"".equals(aqBean.requirement)) {
			%>
			<tr class="redMain" <%=aqBean.getBGColor()%>>
				<td valign="top"><nobr><%=reqText%>:</nobr></td>
				<td colspan="4"><strong><%=aqBean.requirement%></strong></td>
			</tr>
			<%
						} //if "" <> req
					} //if showReq
				}//while
				aqBean.closeList();
			%>
		</table>
		<%
			}//if
		%>
		</td>
	</tr>
</table>
<%
	} finally {
		aqBean.closeList();
	}
%>
</body>
</html>
