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
	UserAccess userAccess = new UserAccess();
	userAccess.setDB("opAccess");
	userAccess.setFromDB(editID);
	boolean wasSubmitted = (null != request.getParameter("submit"));

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
		userAccess.setFromRequest(request);
		userAccess.writeToDB(permissions);
		if (aBean.isOK() && oBean.isOK()) {
			aBean.writeToDB();
			oBean.writeToDB();
			oBean.writeFacilitiesToDB();
			FACILITIES.resetFacilities();
			response.sendRedirect("report_accounts.jsp?type=" + aBean.type);
			return;
		}//if
	}//if
	String errorMsg = "";
%>

<html>
<head>
<title>Edit Operators</title>
<script language="JavaScript" SRC="js/DHTMLUtils.js"
	type="text/javascript"></script>
<script language="JavaScript" SRC="js/verifyInsurance.js"
	type="text/javascript"></script>
</head>
<body>
<h1>Edit <%=aBean.type%>
<span class="sub"><%=aBean.name%></span>
</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a class="current" href="accounts_edit_operator.jsp?id=<%=aBean.id%>">Edit</a></li>
	<% if (!oBean.isCorporate) { %>
	<li><a href="AuditOperator.action?oID=<%=aBean.id%>">Audits</a></li>
	<% } %>
	<li><a href="UsersManage.action?accountId=<%=aBean.id%>">Users</a></li>
	<li><a href="report_accounts.jsp?type=<%=aBean.type%>">Return to List</a></li>
</ul>
</div>

<table width="500" cellpadding="10" cellspacing="0">
	<tr>
		<td valign="top" bgcolor="#FFFFFF" class="blueMain">
		<form name="form1" method="post"
			action="accounts_edit_operator.jsp?id=<%=editID%>"><input
			name="createdBy" type="hidden" value="<%=aBean.createdBy%>">
		<input name="type" type="hidden" value="<%=aBean.type%>">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="2" class="redMain"><b><%=errorMsg%> <%
 	if (request.getParameter("submit") != null)
 		out.println(aBean.getErrorMessages());
 %> </b></td>
			</tr>
			<tr class="blueMain">
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Name</td>
				<td><input name="name" type="text" class="forms" size="30"
					value="<%=aBean.name%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Last Login:</td>
				<td class="redMain" align="left"><%=aBean.lastLogin%></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Contact</td>
				<td><input name="contact" type="text" class="forms" size="20"
					value="<%=aBean.contact%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Address</td>
				<td><input name="address" type="text" class="forms" size="30"
					value="<%=aBean.address%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">City</td>
				<td><input name="city" type="text" class="forms" size="15"
					value="<%=aBean.city%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">State</td>
				<td><%=com.picsauditing.PICS.Inputs.getStateSelect("state", "forms", aBean.state)%></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Zip</td>
				<td><input name="zip" type="text" class="forms" size="7"
					value="<%=aBean.zip%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Phone</td>
				<td><input name="phone" type="text" class="forms" size="15"
					value="<%=aBean.phone%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Phone 2</td>
				<td><input name="phone2" type="text" class="forms" size="15"
					value="<%=aBean.phone2%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Fax</td>
				<td><input name="fax" type="text" class="forms" size="15"
					value="<%=aBean.fax%>"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Email</td>
				<td><input name="email" type="text" class="forms" size="30"
					value="<%=aBean.email%>"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="blueMain">Web URL</td>
				<td class="redMain"><input name="web_URL" type="text"
					class="forms" size="30" value="<%=aBean.web_URL%>"> <br />
				example: www.site.com</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Industry</td>
				<td><%=aBean.getIndustrySelect("industry", "forms", aBean.industry)%></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Visible:</td>
				<td class="blueMain" align="left"><label><input
					name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>Yes</label>
				<label><input name="active" type="radio" value="N"
					<%=aBean.getNotActiveChecked()%>>No</label></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Receive email when
				contractor is activated:</td>
				<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadio("doSendActivationEmail", "forms", oBean.doSendActivationEmail)%>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" class="blueMain">Send Emails to:</td>
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
				<td class="blueMain" align="right">Facilities</td>
				<td><%=aBean.getGeneralSelectMultiple("facilities", "blueMain", oBean.getFacilitiesArray())%>
			</tr>
			<%
				} else {
			%>
			<tr>
				<td class="blueMain" align="right">Sees All:</td>
				<td class="blueMain" align="left"><%=Inputs.getYesNoRadio("seesAllContractors", "forms", oBean.seesAllContractors)%>
				</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Can Add:</td>
				<td class="blueMain" align="left"><%=Inputs.getYesNoRadio("canAddContractors", "forms", oBean.canAddContractors)%>
				</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Contractors pay:</td>
				<td class="blueMain" align="left" valign="bottom"><%=Inputs.getRadioInput("doContractorsPay", "forms", oBean.doContractorsPay,
								OperatorBean.CONTRACTORS_PAY_ARRAY)%>
				</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Approves Contractors:</td>
				<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadio("approvesRelationships", "forms", oBean.approvesRelationships)%>
				</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Sees Ins. Certs:</td>
				<td class="blueMain" align="left" valign="bottom"><%=Inputs.getYesNoRadioWithEvent("canSeeInsurance", "forms", oBean.canSeeInsurance, "onclick",
								"setDisplay", "")%>
				<span id="auditorID" class="display_off"><%=AUDITORS.getAuditorsSelect("insuranceAuditor_id", "forms", oBean.insuranceAuditor_id)%></span>
				</td>
			</tr>
			<%
				}
			%>
			<tr>
				<td>&nbsp;</td>
				<td><input name="submit" type="submit" class="forms"
					value="Save"></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
				<table bgcolor="#EEEEEE" cellspacing="1" cellpadding="1">
					<tr class="whiteTitle">
						<td align="center" bgcolor="#336699" colspan=2>Permission</td>
						<td align="center" bgcolor="#993300">Can Grant</td>
					</tr>
					<%
						for (OpPerms perm : OpPerms.values()) {
					%>
					<tr <%=Utilities.getBGColor(perm.ordinal())%>>
						<td class="blueMain" align="right"><%=perm.ordinal() + 1%></td>
						<td class="blueMain" align="left"><%=perm.getDescription()%></td>
						<td align="center"><input name="perm_<%=perm%>"
							type="checkbox" class="forms" value="checked"
							<%=userAccess.getChecked(perm)%>></td>
					</tr>
					<%
						}//for
					%>
				</table>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input name="submit" type="submit" class="forms"
					value="Save"></td>
			</tr>
		</table>
		</form>
		</td>
	</tr>
</table>
</body>
</html>
