<%@page language="java" errorPage="exception_handler.jsp"%>
<%@page import="java.util.*,com.picsauditing.domain.CertificateDO"%>
<%@include file="includes/main.jsp"%>
<%@include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean"
	scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean"
	scope="page" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"
	scope="page" />
<%
	try {
		permissions.tryPermission(OpPerms.InsuranceApproval);

		SearchFilter filter = new SearchFilter();
		filter.setParams(Utilities.requestParamsToMap(request));

		if (!filter.has("s_certStatus"))
			filter.set("s_certStatus", "Pending");
		if (!filter.has("orderBy"))
			filter.set("orderBy", "name");
		boolean canEdit = permissions.hasPermission(OpPerms.InsuranceApproval, OpType.Edit)
				&& !"Expired".equals(filter.get("s_certStatus"));

		if (canEdit && "Submit".equals(request.getParameter("action"))) {
			List<CertificateDO> list = cerBean.setCertificatesFromCheckList(Utilities
					.requestParamsToMap(request));
			cerBean.updateCertificates(list);
			list = cerBean.sendEmailFromCheckList(Utilities.requestParamsToMap(request));
			cerBean.sendEmail(list, permissions);
		}

		cerBean.setList(permissions, filter);
		sBean.pageResults(cerBean.getListRS(), 50, request);
%>
<html>
<head>
<title>Insurance Approval</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Insurance Approval
<span class="sub">InsureGuard</span></h1>
<form name="form1" method="post" action="report_certificates.jsp">
	<table border="0" align="center" cellpadding="2" cellspacing="0">
		<tr>
				<td><input name="s_accountName" type="text" class="forms"
					value="<%=filter.getInputValue("s_accountName")%>" size="8"
					onFocus="clearText(this)"></td>
				<%
					if (permissions.isAdmin()) {
				%>
				<td><%=new AccountBean().getGeneralSelect3("s_opID", "forms", filter.getInputValue("s_opID"),
									SearchBean.LIST_DEFAULT, "")%></td>
				<%
					}//if
				%>
				<td class="blueMain">&nbsp;&nbsp;Status</td>
				<td><%=com.picsauditing.PICS.Utilities.inputSelect2("s_certStatus", "forms", filter
								.getInputValue("s_certStatus"), CertificateBean.STATUS_ARRAY)%></td>
				<td><input name="imageField" type="image"
					src="images/button_search.gif" width="70" height="23" border="0"
					onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)"
					onMouseOut="MM_swapImgRestore()"></td>
		</tr>
	</table>
</form>
<form name="emailForm" method="post" action="report_certificates.jsp"><br>
<div><%=sBean.getLinks(filter.getURLQuery())%></div>
<%
	if (canEdit) {
%> <input name="action" type="submit" class="buttons"
	value="Submit"> <input name="s_certStatus" type="hidden"
	value="<%=filter.getInputValue("s_certStatus")%>"> <%
 	}//if
 %>
<table id="certTable" class="report">
	<thead><tr> 
				<td bgcolor="#003366">Num</td>
				<%
					if (canEdit) {
							if (!"Approved".equals(filter.getInputValue("s_certStatus"))) {
				%>
				<td bgcolor="#003366" align="center">Approve</td>
				<%
					} else {
				%>
				<td bgcolor="#003366" align="center">Resend</td>
				<%
					}//else
							if (!"Rejected".equals(filter.getInputValue("s_certStatus"))) {
				%>
				<td bgcolor="#003366" align="center">Reject</td>
				<%
					} else {
				%>
				<td bgcolor="#003366" align="center">Resend</td>
				<%
					}//else
				%>
				<%
					} else {
				%>
				<td bgcolor="#003366">Status</td>
				<%
					}//else
				%>
				<td bgcolor="#003366">Reason</td>
				<td bgcolor="#003366">Contractor</td>
				<td bgcolor="#003366">Type</td>
				<%
					if (permissions.isAdmin() || permissions.isCorporate()) {
				%>
				<td bgcolor="#003366">Operator</td>
				<%
					}//if
				%>
				<td bgcolor="#003366">Expires</td>
				<td bgcolor="#003366">Liability</td>
				<td bgcolor="#003366">Add'l Named Ins.</td>
				<td bgcolor="#003366">Waiver</td>
				<td align="center" bgcolor="#993300">File</td>
	</tr></thead>

	<%
		while (sBean.isNextRecord(certDO)) {
	%>
	<tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%>
				class="<%=sBean.getTextColor()%>">
				<td align="right"><%=sBean.count - 1%></td>
				<%
					if (canEdit) {
				%>
				<%
					if ("Pending".equals(filter.getInputValue("s_certStatus"))) {
				%>
				<td align="center"><input
					name="status_<%=certDO.getCert_id()%>" class=buttons type=radio
					value="Approved"></td>
				<td align="center"><input
					name="status_<%=certDO.getCert_id()%>" class=buttons type=radio
					value="Rejected"></td>
				<%
					} else {
				%>
				<td align="center"><%=Inputs.getCheckBoxInput("status_" + certDO.getCert_id(), "buttons", "",
											"Approved")%></td>
				<td align="center"><%=Inputs.getCheckBoxInput("status_" + certDO.getCert_id(), "buttons", "",
											"Rejected")%></td>
				<%
					}//if
				%>
				<td><input type="text" id="reason_<%=certDO.getCert_id()%>"
					name="reason_<%=certDO.getCert_id()%>" class="forms"
					value="<%=certDO.getReason()%>" /> <input type="hidden"
					name="operator_id_<%=certDO.getCert_id()%>"
					value="<%=certDO.getOperator_id()%>" /> <input type="hidden"
					name="contractor_id_<%=certDO.getCert_id()%>"
					value="<%=certDO.getContractor_id()%>" /> <input type="hidden"
					name="type_<%=certDO.getCert_id()%>" value="<%=certDO.getType()%>" />
				</td>
				<%
					} else {
				%>
				<td><%=certDO.getStatus()%></td>
				<td><%=certDO.getReason()%></td>
				<%
					}//else
							if (permissions.isCorporate()) {
				%>
				<td>&nbsp;&nbsp;</td>
				<%
					}//if
				%>
				<td><a
					href="contractor_upload_certificates.jsp?id=<%=certDO.getContractor_id()%>"
					class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
				<td><%=certDO.getType()%></td>
				<%
					if (permissions.isAdmin() || permissions.isCorporate()) {
				%>
				<td><%=certDO.getOperator()%></td>
				<%
					}//if
				%>
				<td><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getExpDate())%></td>
				<td id="liability" align="right"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%></td>
				<td id="namedInsured"><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%></td>
				<td id="subrogation"><%=certDO.getSubrogationWaived()%></td>
				<td align="center"><a
					href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt()%>"
					target="_blank"> <img src="images/icon_insurance.gif"
					width="20" height="20" border="0" alt=""></a></td>
	</tr>
	<%
		}//while
	%>
</table>
<div><%=sBean.getLinks(filter.getURLQuery())%></div>
<br>
<%
	if (canEdit) {
%> <input name="action" type="submit" class="buttons"
	value="Submit"> <input name="s_certStatus" type="hidden"
	value="<%=filter.getInputValue("s_certStatus")%>"> <%
 	}//if
 %>
</form>
</body>
</html>
<%
	} finally {
		cerBean.closeList();
		sBean.closeSearch();
	}//finally
%>
