<%@page language="java"%>
<%
	//@page language="java" errorPage="exception_handler.jsp"
%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="prBean"
	class="com.picsauditing.PICS.pqf.RequirementBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />
<%
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String id = conID;

	try {
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		prBean.setList(action.getAuditID());
%>
<html>
<head>
<title>Audit Requirements</title>
</head>
<body>
<%@ include file="utilities/adminOperatorContractorNav.jsp"%>

<table class="report">
	<tr class="whiteTitle">
		<td width="30" bgcolor="#003366">#</td>
		<td bgcolor="#003366">Requirement</td>
	</tr>
	<%
		while (prBean.isNextRecord()) {
	%>
	<tr <%=prBean.getBGColor()%> class=blueMain>
		<td valign=top align=right>Number:</td>
		<td valign=top><%=prBean.count%></td>
	</tr>
	<tr <%=prBean.getBGColor()%> class=blueMain>
		<td valign=top align=right>Category:</td>
		<td valign=top><%=prBean.pcBean.category%></td>
	</tr>
	<tr <%=prBean.getBGColor()%> class=blueMain>
		<td valign=top align=right>Req:</td>
		<td valign=top class=<%=prBean.getReqStyle()%>><strong><%=prBean.requirement1%></strong></td>
	</tr>
	<tr <%=prBean.getBGColor()%> class=blueMain>
		<td valign=top align=right>Links:</td>
		<td valign=top><%=prBean.pqBean.getLinks()%></td>
	</tr>
	<tr <%=prBean.getBGColor()%> class=blueMain>
		<td valign=top align=right>Status:</td>
		<td valign=top><%=prBean.getStatus()%></td>
	</tr>
	<%
		}//while
			prBean.closeList();
	%>
</table>
<%
	} finally {
		prBean.closeList();
	}
%>
</body>
</html>
