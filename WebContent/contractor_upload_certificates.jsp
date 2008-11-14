<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean"
	scope="page" />
<jsp:setProperty name="cerBean" property="*" />
<script src="js/Validate.js"></script>
<script src="js/ValidateForms.js"></script>
<%
	try {
		SearchFilter filter = new SearchFilter();
		if (!permissions.isContractor())
			permissions.tryPermission(OpPerms.InsuranceCerts);

		boolean canEdit = permissions.hasPermission(
				OpPerms.InsuranceCerts, OpType.Edit)
				|| permissions.isContractor();
		boolean canDelete = permissions.hasPermission(
				OpPerms.InsuranceCerts, OpType.Delete)
				|| permissions.isContractor();
		String id = request.getParameter("id");
		String conID = id;
		if (request.getParameter("action") != null)
			cerBean.processForm(pageContext, permissions);
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(id);
		cBean.tryView(permissions);

		filter.set("s_conID", id);
		cerBean.setList(permissions, filter);
%>
<html>
<head>
<title>Edit Insurance Certificates</title>
<meta name="header_gif" content="header_insurance.gif" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<% request.setAttribute("subHeading", "Contractor Insurance"); %>
<%@ include file="includes/conHeaderLegacy.jsp"%>
<table border="0" cellpadding="15" cellspacing="1">
	<tr>
		<td align="center" valign="top" class="redMain"><%=cerBean.getErrorMessages()%>
		<%
			if (canEdit) {
		%>
		<form name="addForm" method="post"
			action="contractor_upload_certificates.jsp?id=<%=id%>&action=add"
			enctype="multipart/form-data">
		<table cellpadding="2" cellspacing="3" bgcolor="FFFFFF">
			<tr>
				<td class="blueMain">Type&nbsp;&nbsp; <%=Utilities.inputSelect("types", "forms",
											"Workers Compensation",
											cerBean.TYPE_ARRAY)%></td>
				<td class="blueMain">Operator&nbsp;&nbsp; <%
 	if (permissions.isOperator()) {
 %>
				<input type="hidden" name="operator_id"
					value="<%=permissions.getAccountId()%>"><%=FACILITIES.getNameFromID(permissions
										.getAccountIdString())%>
				<%
					} else {
				%> <%=new AccountBean().getGeneralSelect3(
										"operator_id", "forms",
										cerBean.operator_id,
										SearchBean.DONT_LIST_DEFAULT, id)%></td>
				<%
					}//else
				%>
			</tr>
			<tr>
				<td class="blueMain">File (.pdf, .doc or .txt)&nbsp;&nbsp; <input
					name="certificateFile" type="FILE" class="forms" size="15" required>
				</td>
				<td class="blueMain">Expiration&nbsp;&nbsp;<%=Inputs.inputSelect2("expMonth", "forms",
									cerBean.expMonth, Inputs.MONTHS_ARRAY)%>
				/<%=Inputs.inputSelect("expDay", "forms",
									cerBean.expDay, Inputs.DAYS_ARRAY)%>/<%=Inputs.inputSelect("expYear", "forms",
									cerBean.expYear, Inputs.YEARS_ARRAY)%>
				</td>
			</tr>
			<tr>
				<td class="blueMain">Liability Limit&nbsp;&nbsp; <input
					type="text" name="liabilityLimit" onchange="validateNumber()"
					class="formsNumber" required
					value="<%=cerBean.getLiabilityLimit()%>"></td>
				<td class="blueMain">Additional Named Insured&nbsp;&nbsp; <input
					type="text" required name="namedInsured" class="forms"
					value="<%=cerBean.getNamedInsured()%>"></td>
			</tr>
			<tr>
				<td class="blueMain">Waiver of Subrogation&nbsp;&nbsp; <input
					type="radio" name="subrogationWaived" CHECKED class="forms"
					value="No" />No&nbsp;&nbsp; <input type="radio"
					name="subrogationWaived" class="forms" value="Yes" />Yes</td>
			</tr>
		</table>
		<hr>
		<input name="Submit" type="submit" class="forms"
			value="Add Certificate"></form>
		<%
			}//if
		%>
		<table class="report">
			<thead><tr>
				<%
					if (canDelete) {
				%>
				<td>Delete</td>
				<%
					}//if
				%>
				<td>Type</td>
				<td>Facility</td>
				<td>Verified</td>
				<td>Status</td>
				<td>Expires</td>
				<td>Liability</td>
				<td>Named Ins.</td>
				<td>Waiver</td>
				<td>File</td>
			</tr></thead>
			<%
				while (cerBean.isNextRecord()) {
			%>
			<tr>
				<%
					if (canDelete) {
				%>
				<form name="deleteForm" method="post"
					action="contractor_upload_certificates.jsp?id=<%=id%>&action=delete">
				<td><input name="delete_id" type="hidden"
					value="<%=cerBean.cert_id%>"> <input name="Submit"
					type="submit" class="forms" value="Del"
					onClick="return confirm('Are you sure you want to delete this file?');">
				</td>
				</form>
				<%
					}//if
				%>
				<td><%=cerBean.type%></td>
				<td><%=FACILITIES.getNameFromID(cerBean.operator_id)%></td>
				<td align="center"><%=cerBean.verified%></td>
				<td align="center"><%=cerBean.status%></td>
				<td><%=cerBean.getExpDateShow()%></td>
				<td><%=java.text.NumberFormat.getInstance().format(
									cerBean.getLiabilityLimit())%></td>
				<td><%=cerBean.getNamedInsured()%></td>
				<td align="center"><%=cerBean.getSubrogationWaived()%></td>
				<td><a
					href="<%=cerBean.getDirPath()%>cert_<%=id%>_<%=cerBean.cert_id%>.<%=cerBean.getExt()%>"
					target="_blank"> <img src="images/icon_insurance.gif"
					width="20" height="20" border="0"></a></td>
			</tr>
			<%
				}//while
			%>
		</table>
		<br>
		<br>
		</td>
	</tr>
</table>
<%
	} finally {
		cerBean.closeList();
	}//finally
%>
</body>
</html>
