<%@ page language="java"
	import="com.picsauditing.PICS.*,com.picsauditing.access.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean"
	scope="page" />
<%@page import="java.util.Random"%>
<%
	permissions.tryPermission(OpPerms.ManageOperators);

	String editID = request.getParameter("id");
	boolean wasSubmitted = (null != request.getParameter("button"));

	aBean.setFromDB(editID);
	if ("Corporate".equals(aBean.type))
		oBean.isCorporate = true;
	oBean.setFromDB(editID);

	if (wasSubmitted) {
		permissions.tryPermission(OpPerms.ManageOperators, OpType.Edit);
		aBean.setFromRequest(request);
		aBean.username = Integer.toString(new Random().nextInt());
		aBean.password = Integer.toString(new Random().nextInt());
		oBean.setFromRequest(request);
		if (aBean.isOK() && oBean.isOK()) {
			aBean.writeToDB();
			oBean.writeToDB();
			oBean.writeFacilitiesToDB();
			FACILITIES.resetFacilities();
			response.sendRedirect("ReportAccountList.action?accountType=" + aBean.type);
			return;
		}//if
	}//if
	String errorMsg = "";
%>
<%@page import="com.picsauditing.dao.UserDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.jpa.entities.User"%>
<%@page import="com.picsauditing.jpa.entities.YesNo"%>
<html>
<head>
<title>Edit <%=aBean.type%> <%=aBean.name%></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>
<h1>Edit <%=aBean.type%> <span class="sub"><%=aBean.name%></span></h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a class="current"
		href="accounts_edit_operator.jsp?id=<%=aBean.id%>">Edit</a></li>
	<%
		if (!oBean.isCorporate) {
	%>
	<li><a href="AuditOperator.action?oID=<%=aBean.id%>">Audits</a></li>
	<%
		}
	%>
	<li><a href="UsersManage.action?accountId=<%=aBean.id%>">Users</a></li>
	<li><a href="op_editFlagCriteria.jsp?opID=<%=aBean.id%>">Flag
	Criteria</a></li>
	<li><a href="ReportAccountList.action?accountType=<%=aBean.type%>">Return
	to List</a></li>
</ul>
</div>

<form name="form1" method="post" class="forms"
	action="accounts_edit_operator.jsp?id=<%=editID%>"><input
	name="createdBy" type="hidden" value="<%=aBean.createdBy%>"> <input
	name="type" type="hidden" value="<%=aBean.type%>">
<table class="forms">
	<tr class="odd">
		<th></th>
		<td class="center">
		<div class="buttons">
		<button name="button" type="submit" class="positive" value="save">Save</button>
		</div>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="redMain"><b><%=errorMsg%> <%
 	if (request.getParameter("submit") != null)
 		out.println(aBean.getErrorMessages());
 %> </b></td>
	</tr>
	<tr class="odd">
		<th>Operator Name:</th>
		<td><input name="name" type="text" class="forms" size="50"
			value="<%=aBean.name%>"></td>
	</tr>
	<tr>
		<th>Visible:</th>
		<td class="blueMain" align="left"><label><input
			name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>Yes</label>
		<label><input name="active" type="radio" value="N"
			<%=aBean.getNotActiveChecked()%>>No</label></td>
	</tr>
	<tr class="odd">
		<th>Primary Contact:</th>
		<td><input name="contact" type="text" class="forms" size="20"
			value="<%=aBean.contact%>"></td>
	</tr>
	<tr>
		<th>Address:</th>
		<td><input name="address" type="text" class="forms" size="30"
			value="<%=aBean.address%>"></td>
	</tr>
	<tr class="odd">
		<th>City:</th>
		<td><input name="city" type="text" class="forms" size="15"
			value="<%=aBean.city%>"></td>
	</tr>
	<tr>
		<th>State:</th>
		<td><%=com.picsauditing.PICS.Inputs.getStateSelect("state", "forms", aBean.state)%></td>
	</tr>
	<tr class="odd">
		<th>Zip:</th>
		<td><input name="zip" type="text" class="forms" size="7"
			value="<%=aBean.zip%>"></td>
	</tr>
	<tr>
		<th>Phone:</th>
		<td><input name="phone" type="text" class="forms" size="15"
			value="<%=aBean.phone%>"></td>
	</tr>
	<tr class="odd">
		<th>Phone 2:</th>
		<td><input name="phone2" type="text" class="forms" size="15"
			value="<%=aBean.phone2%>"></td>
	</tr>
	<tr>
		<th>Fax:</th>
		<td><input name="fax" type="text" class="forms" size="15"
			value="<%=aBean.fax%>"></td>
	</tr>
	<tr class="odd">
		<th>Email:</th>
		<td><input name="email" type="text" class="forms" size="30"
			value="<%=aBean.email%>"></td>
	</tr>
	<tr>
		<th>Web URL:</th>
		<td class="redMain"><input name="web_URL" type="text"
			class="forms" size="30" value="<%=aBean.web_URL%>"> <br />
		example: www.site.com</td>
	</tr>
	<tr class="odd">
		<th>&nbsp;</th>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<th>Industry:</th>
		<td><%=aBean.getIndustrySelect("industry", "forms", aBean.industry)%></td>
	</tr>
	<tr class="odd">
		<th>&nbsp;
		</th>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<th>Receive email when<br />
		contractor is activated:</th>
		<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadio("doSendActivationEmail", "forms", oBean.doSendActivationEmail)%>
		</td>
	</tr>
	<tr class="odd">
		<th>Send Emails to:</th>
		<td class="redMain"><input name="activationEmails" type="text"
			class="forms" size="30" value="<%=oBean.activationEmails%>">
		<br>
		separate emails with commas <br>
		example: a@bb.com, c@dd.com</td>
	</tr>
	<%
		if (oBean.isCorporate) {
	%>
	<tr>
		<th>Facilities:</th>
		<td><%=aBean.getGeneralSelectMultiple("facilities", "blueMain", oBean.getFacilitiesArray())%>
	</tr>
	<%
		} else {
	%>
	<tr>
		<th>Contractors pay:</th>
		<td class="blueMain" align="left" valign="bottom"><%=Inputs.getRadioInput("doContractorsPay", "forms", oBean.doContractorsPay,
								OperatorBean.CONTRACTORS_PAY_ARRAY)%></td>
	</tr>
	<tr class="odd">
		<th>Approves Contractors:</th>
		<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadio("approvesRelationships", "forms", oBean.approvesRelationships)%>
		</td>
	</tr>
	<tr>
		<th>Sees Ins. Certs:</th>
		<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadio("canSeeInsurance", "forms", oBean.canSeeInsurance)%>
		<span id="auditorID"><%=AUDITORS.getAuditorsSelect("insuranceAuditor_id", "forms", oBean.insuranceAuditor_id)%></span>
		</td>
	</tr>
	<%
		}
	%>
	<tfoot>
		<tr class="odd">
			<th></th>
			<td class="center">
			<div class="buttons">
			<button name="button" type="submit" class="positive" value="save">Save</button>
			</div>
			</td>
		</tr>
	</tfoot>
</table>
</form>
</body>
</html>
