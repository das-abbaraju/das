<%@page language="java" errorPage="exception_handler.jsp"%>
<%@page import="java.util.*,com.picsauditing.domain.CertificateDO"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean"
	scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean"
	scope="page" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"
	scope="page" />
<%
	permissions.tryPermission(OpPerms.InsuranceCerts);

	try {
		String[] statusList = new String[] { "Neither",
				"Requires Action", "Approved", "Approved", "Rejected",
				"Rejected" };
		String status = request.getParameter("status");
		if (status == null)
			status = "Neither";

		String id = request.getParameter("operator_id");
		String operator_id = request.getParameter("id");
		if (operator_id != null)
			id = operator_id;

		if (permissions.isOperator() || permissions.isCorporate())
			id = permissions.getAccountIdString();

		cerBean.operator_id = id;

		cerBean.contractor_name = request.getParameter("name");
		cerBean.setStatus(status);

		if ("Submit".equals(request.getParameter("Submit"))) {
			if (permissions.isOperator()) {
				List<CertificateDO> list = cerBean
						.setCertificatesFromCheckList(request);
				cerBean.UpdateCertificates(list);
				list = cerBean.sendEmailFromCheckList(request);
				cerBean.sendEmail(list, permissions);
			}
		}

		cerBean.contractor_name = request.getParameter("name");
		cerBean.setStatus(status);

		if (permissions.isCorporate())
			cerBean.setListByFacilities(id);
		else
			cerBean.setListAll(id);

		sBean.pageResults(cerBean.getListRS(), 20, request);

		pageBean.setTitle("Insurance Certificates");
%>
<%@ include file="includes/header.jsp"%>
<%@ include file="includes/selectReport.jsp"%>
<span class="blueHeader">Insurance Certificates Report</span>
<br>
<form name="form1" method="post" action="report_certificates.jsp">

<table border="0" align="center" cellpadding="2" cellspacing="0">
	<tr>
		<td><input name="name" type="text" class="forms"
			value="<%=cerBean.contractor_name%>" size="8"
			onFocus="clearText(this)"></td>
		<%
			if (permissions.isAdmin()) {
		%>
		<td><%=new AccountBean().getGeneralSelect3(
									"operator_id", "forms",
									cerBean.operator_id,
									SearchBean.LIST_DEFAULT, "")%></td>
		<%
			}
		%>
		<td class="blueMain">&nbsp;&nbsp;Status</td>
		<td><%=com.picsauditing.PICS.Utilities.inputSelect2(
								"status", "forms", cerBean.getStatus(),
								statusList)%></td>
		<td><input name="imageField" type="image"
			src="images/button_search.gif" width="70" height="23" border="0"
			onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)"
			onMouseOut="MM_swapImgRestore()"></td>
	</tr>
</table>
</form>
<form name="emailForm" method="post" action="report_certificates.jsp"><br>
<br>
<%=sBean.getLinks()%>
<table ID="certTable" width="100%" border="0" cellpadding="1"
	cellspacing="1">
	<tr class="whiteTitle">
		<td bgcolor="#003366">&nbsp;</td>
		<%
			if (permissions.isOperator()) {
		%>
		<td bgcolor="#003366">&nbsp;</td>
		<%
			}
		%>
		<%
			if (permissions.isAdmin()) {
		%>
		<td bgcolor="#003366">Sent</td>
		<td bgcolor="#003366">Last Sent</td>
		<%
			}
		%>
		<td bgcolor="#003366">Contractor</td>
		<td bgcolor="#003366">Type</td>
		<%
			if (!permissions.isOperator()) {
		%>
		<td bgcolor="#003366">Operator</td>
		<%
			}
		%>
		<td bgcolor="#003366">Expires</td>
		<td bgcolor="#003366">Liability</td>
		<td bgcolor="#003366">Add'l Named Ins.</td>
		<td bgcolor="#003366">Waiver</td>
		<td align="center" bgcolor="#993300">File</td>
	</tr>
	<%
		while (sBean.isNextRecord(certDO)) {
	%>
	<tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%>
		class="<%=sBean.getTextColor()%>">
		<%
			if (permissions.isOperator()) {
		%>
		<td></td>
		<td><nobr> <label><input
			name="status_<%=certDO.getCert_id()%>" class="buttons" type="radio"
			value="<%=certDO.getStatus()%>"
			<%= ("Approved".equals(certDO.getStatus()))? "checked": "" %> />
		Approve</label> <label><input name="status_<%=certDO.getCert_id()%>"
			class="buttons" type="radio" value="<%=certDO.getStatus()%>"
			<%= ("Rejected".equals(certDO.getStatus()))? "checked": "" %> />
		Reject</label> &nbsp;&nbsp;Reason: <input type="text"
			id="reason_<%=certDO.getCert_id()%>"
			name="reason_<%=certDO.getCert_id()%>" class="forms"
			value="<%=certDO.getReason()%>" /> <input type="hidden"
			name="operator_id_<%=certDO.getCert_id()%>"
			value="<%=certDO.getOperator_id()%>" /> <input type="hidden"
			name="contractor_id_<%=certDO.getCert_id()%>"
			value="<%=certDO.getContractor_id()%>" /> <input type="hidden"
			name="type_<%=certDO.getCert_id()%>" value="<%=certDO.getType()%>" />
		</nobr></td>
		<%
			}
		%>
		<%
			if (permissions.isAdmin()) {
		%>
		<td><input name="sendEmail_<%=certDO.getCert_id()%>"
			type="checkbox"></td>
		<td><%=certDO.getSent()%></td>
		<td align="center"><%=com.picsauditing.PICS.DateBean
												.toShowFormat(certDO
														.getLastSentDate())%></td>
		<%
			}
		%>
		<%
			if (permissions.isCorporate()) {
		%>
		<td>&nbsp;&nbsp;</td>
		<%
			}
		%>
		<td><a
			href="contractor_detail.jsp?id=<%=certDO.getContractor_id()%>"
			class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
		<td><%=certDO.getType()%></td>
		<%
			if (permissions.isAdmin() || permissions.isCorporate()) {
		%>
		<td><%=certDO.getOperator()%></td>
		<%
			}
		%>
		<td align="center"><%=com.picsauditing.PICS.DateBean
									.toShowFormat(certDO.getExpDate())%></td>
		<%
			if (permissions.isAdmin() || permissions.isCorporate()) {
		%>
		<td align="right"><%=java.text.NumberFormat.getInstance().format(
										certDO.getLiabilityLimit())%></td>
		<td><%=com.picsauditing.PICS.Utilities
										.convertNullString(certDO
												.getNamedInsured(), "None")%></td>
		<td><%=certDO.getSubrogationWaived()%></td>
		<%
			}
		%>
		<%
			if (permissions.isOperator()) {
		%>
		<td id="liability" align="right"><%=java.text.NumberFormat.getInstance().format(
										certDO.getLiabilityLimit())%></td>
		<td id="namedInsured"><%=com.picsauditing.PICS.Utilities
										.convertNullString(certDO
												.getNamedInsured(), "None")%></td>
		<td id="subrogation"><%=certDO.getSubrogationWaived()%></td>
		<%
			}
		%>
		<td align="center"><a
			href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt()%>"
			target="_blank"> <img src="images/icon_insurance.gif" width="20"
			height="20" border="0" alt=""></a></td>
	</tr>
	<%
		}//while
	%>
</table>
<br>
<center><%=sBean.getLinks()%></center>
<br>
<%
	if (permissions.isAdmin()) {
%> <input name="Submit" type="submit" class="buttons"
	value="Send Emails"
	onClick="return confirm('Are you sure you want to send these emails?');">
<%
	}
%> <%
 	if (permissions.isOperator()) {
 %> <input name="Submit" type="submit" class="buttons" value="Submit">
<%
 	}
 %>
</form>
<%
	} finally {
		cerBean.closeList();
		sBean.closeSearch();
	}
%>
<%@ include file="includes/footer.jsp"%>
