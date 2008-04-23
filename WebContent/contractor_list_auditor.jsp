<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean"
	scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean"
	scope="page" />
<%
	if (!pBean.isAuditor())
		throw new NoRightsException("PICS Auditor");

	tBean.setFromDB();
	sBean.orderBy = request.getParameter("orderBy");
	if (null == sBean.orderBy)
		sBean.orderBy = "name";

	try {
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, permissions.getUserIdString());
%>
<html>
<head>
<title>Contractors for Auditors</title>
<script src="js/Search.js" type="text/javascript"></script>
</head>
<body>
<form name="form1" id="form1" method="post"
	action="contractor_list_auditor.jsp">
<table border="0" cellpadding="2" cellspacing="0">
	<tr align="center">
		<td><input name="name" type="text" class="forms"
			value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
		<td><%=com.picsauditing.PICS.Inputs.inputSelectFirst("auditType", "forms", sBean.selected_auditType,
								com.picsauditing.PICS.pqf.Constants.AUDIT_TYPE_ARRAY,
								com.picsauditing.PICS.pqf.Constants.DEFAULT_AUDIT)%></td>
		<td><input name="imageField" type="image"
			src="images/button_search.gif" width="70" height="23" border="0"></td>
	</tr>
</table>

<input type="hidden" name="showPage" value="1" /> <input type="hidden"
	name="startsWith" value="" /> <input type="hidden" name="orderBy"
	value="<%=sBean.orderBy == null ? "" : sBean.orderBy %>" /></form>
</td>
</tr>
<tr>
	<td></td>
	<td align="center" class="blueMain">You have <strong><%=sBean.getNumResults()%></strong>
	contractors to audit | &nbsp;<%=sBean.getLinksWithDynamicForm()%></td>
</tr>
<tr>
	<td colspan="2" align="center"><a
		href="audit_calendar.jsp?format=popup&id=<%=pBean.userID%>"
		target="_blank" class="blueMain">Audit Calendar</a></td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>
</tr>
</table>
<table width="657" border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2 bgcolor="#003366"></td>
		<td align="center" bgcolor="#336699">PQF</td>
		<td align="center" bgcolor="#6699CC" colspan=4>Desktop Audit</td>
		<td align="center" bgcolor="#336699" colspan=4>Office Audit</td>
		<td align="center" bgcolor="#336699" colspan=4>D&A Audit</td>
	</tr>
	<tr>
		<td colspan=2 bgcolor="#003366"><a
			href="javascript: changeOrderBy('form1','name');" class="whiteTitle">Contractor</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','pqfSubmittedDate DESC');"
			class="whiteTitleSmall">Submit</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','desktopAssignedDate DESC');"
			class="whiteTitleSmall">Assign</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','desktopAuditor_id DESC');"
			class="whiteTitleSmall">Auditor</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','desktopSubmittedDate DESC');"
			class="whiteTitleSmall">Perform</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','desktopClosedDate DESC');"
			class="whiteTitleSmall">Close</a></td>

		<td align="center" bgcolor="#336699"><a
			href="javascript: changeOrderBy('form1','assignedDate DESC');"
			class="whiteTitleSmall">Assign</a></td>
		<td align="center" bgcolor="#336699"><a
			href="javascript: changeOrderBy('form1','auditor_id DESC');"
			class="whiteTitleSmall">Auditor</a></td>
		<td align="center" bgcolor="#336699"><a
			href="javascript: changeOrderBy('form1','auditCompletedDate DESC');"
			class="whiteTitleSmall">Perform</a></td>
		<td align="center" bgcolor="#336699"><a
			href="javascript: changeOrderBy('form1','auditClosedDate DESC');"
			class="whiteTitleSmall">Close</a></td>

		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','daAssignedDate DESC');"
			class="whiteTitleSmall">Assign</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','daAuditor_id DESC');"
			class="whiteTitleSmall">Auditor</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','daSubmittedDate DESC');"
			class="whiteTitleSmall">Perform</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="javascript: changeOrderBy('form1','daClosedDate DESC');"
			class="whiteTitleSmall">Close</a></td>
	</tr>
	<%
		while (sBean.isNextRecord()) {
				String auditStatus = "";
				if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(sBean.selected_auditType)) {
					if (sBean.cBean.isDesktopClosed())
						auditStatus = ContractorBean.AUDIT_STATUS_CLOSED;
				} else if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(sBean.selected_auditType))
					auditStatus = sBean.cBean.auditStatus;
	%>
	<tr <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
		<%
			if (auditStatus.equals(ContractorBean.AUDIT_STATUS_CLOSED)) {
		%>
		<td><%=sBean.count - 1%></td>
		<td><span class="cantSee"><%=sBean.aBean.name%></span></td>
		<%
			} else {
		%>
		<td><%=sBean.count - 1%></td>
		<td><a href="ContractorView.action?id=<%=sBean.aBean.id%>"
			title="view <%=sBean.aBean.name%> details"
			class="<%=sBean.getTextColor()%>"><%=sBean.aBean.name%></a></td>
		<%
			} //else
		%>
		<td align="center"><%=sBean.cBean.pqfSubmittedDate%></td>
		<td align="center"><%=sBean.cBean.desktopAssignedDate%></td>
		<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.desktopAuditor_id)%></td>
		<td align="center"><%=sBean.cBean.desktopSubmittedDate%></td>
		<td align="center"><%=sBean.cBean.desktopClosedDate%></td>

		<td align="center"><%=sBean.cBean.assignedDate%></td>
		<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.auditor_id)%></td>
		<td align="center"><%=sBean.cBean.auditCompletedDate%></td>
		<td align="center"><%=sBean.cBean.auditClosedDate%></td>

		<td align="center"><%=sBean.cBean.daAssignedDate%></td>
		<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.daAuditor_id)%></td>
		<td align="center"><%=sBean.cBean.daSubmittedDate%></td>
		<td align="center"><%=sBean.cBean.daClosedDate%></td>
	</tr>
	<%
		} // while
	%>
</table>
<br>
<center><span class="redMain"><%=sBean.getLinksWithDynamicForm()%></span></center>
</td>
</tr>
</table>
<br>
<center><%@ include file="utilities/contractor_key.jsp"%><br>
<br>
<span class="blueMain"> You must have <a
	href="http://www.adobe.com/products/acrobat/readstep2.html"
	target="_blank">Adobe Reader 6.0</a> or later to view the documents
above.</span></center>
<%
	} finally {
		sBean.closeSearch();
	}
%>
</body>
</html>