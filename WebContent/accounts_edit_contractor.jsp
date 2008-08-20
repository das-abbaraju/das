<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean"
	scope="page" />
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean"
	scope="page" />
<jsp:useBean id="helper"
	class="com.picsauditing.servlet.upload.UploadConHelper" />
<%@page import="com.picsauditing.util.SpringUtils"%>
<%
	permissions.tryPermission(OpPerms.ContractorAccounts);
	String id = request.getParameter("id");
	String editID = request.getParameter("id");

	String conID = id;
	boolean isRemoved = "Yes".equals(request.getParameter("Remove"));
	boolean isSubmitted = "Yes".equals(request.getParameter("isSubmitted"));
	String msg = "";
	aBean.setFromDB(editID);
	cBean.setFromDB(editID);

	if (isSubmitted) {
		permissions.tryPermission(OpPerms.ContractorAccounts, OpType.Edit);
		//Process form upload
		request.setAttribute("uploader", String
				.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.CONTRACTOR));
		request.setAttribute("directory", "files");
		request.setAttribute("exts", "pdf,doc,txt,jpg,jpeg,gif");
		helper.init(request, response);

		aBean.setFromUploadRequest(request);
		cBean.setFromUploadRequest(request);

		String errorMsg1 = (String) request.getAttribute("error_logo");
		String errorMsg2 = (String) request.getAttribute("error_brochure");
		if (errorMsg1 != null && errorMsg1 != "")
			aBean.getErrors().addElement(errorMsg1);
		if (errorMsg2 != null && errorMsg2 != "")
			aBean.getErrors().addElement(errorMsg2);
		if (aBean.getErrors().size() == 0) {
			if (aBean.isOK() && cBean.isOK()) {
				java.util.Map<String, String> params = (java.util.Map<String, String>) request
						.getAttribute("uploadfields");
					aBean.writeToDB();
					cBean.setUploadedFiles(request);
					cBean.writeToDB();
					cBean.buildAudits();
					
					BillContractor billing = new BillContractor();
					billing.setContractor(id);
					billing.calculatePrice();
					billing.writeToDB();
					FlagCalculator2 flagCalc = (FlagCalculator2)SpringUtils.getBean("FlagCalculator2");
					flagCalc.runByContractor(Integer.parseInt(editID));
					response.sendRedirect("ContractorView.action?id=" + editID);
					return;
			}
		}
	}
	
	if(isRemoved) {
		aBean.deleteAccount(id, config.getServletContext().getRealPath("/"));
		response.sendRedirect("ContractorListAdmin.action");
		return; 
	}
	
%>
<html>
<head>
<title>Edit Contractor</title>
<meta name="contextID" content="637" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
	var cal1 = new CalendarPopup('caldiv1');
	cal1.offsetY = -110;
	cal1.setCssPrefix("PICS");
	cal1.showNavigationDropdowns();
</SCRIPT>
</head>
<body>
<% request.setAttribute("subHeading", "Contractor Edit"); %>
<%@ include file="includes/conHeaderLegacy.jsp"%>
<form name="form1" method="post"
	action="accounts_edit_contractor.jsp?id=<%=editID%>&isSubmitted=Yes"
	enctype="multipart/form-data"><input name="createdBy"
	type="hidden" value="<%=aBean.createdBy%>"><input name="type"
	type="hidden" value="Contractor">
	<input name="submit" type="submit" class="forms" value="Save Contractor">
<div class="redMain">
<%
	if (isSubmitted)
		out.println(aBean.getErrorMessages() + cBean.getErrorMessages());
%>
</div>
<div style="float: left;">
<table width="0" border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td class="blueMain" align="right">Name</td>
		<td><input name="name" type="text" class="forms" size="50"
			value="<%=aBean.name%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Address</td>
		<td><input name="address" type="text" class="forms" size="45"
			value="<%=aBean.address%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">City</td>
		<td><input name="city" type="text" class="forms" size="20"
			value="<%=aBean.city%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">State/Province</td>
		<td><%=Inputs.getStateSelect("state", "forms", aBean.state)%></td>
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
		<td class="blueMain" align="right">Contact</td>
		<td><input name="contact" type="text" class="forms" size="20"
			value="<%=aBean.contact%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Second Contact</td>
		<td><input name="secondContact" type="text" class="forms"
			size="20" value="<%=cBean.secondContact%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Second Phone</td>
		<td><input name="secondPhone" type="text" class="forms" size="15"
			value="<%=cBean.secondPhone%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Second Email</td>
		<td><input name="secondEmail" type="text" class="forms" size="20"
			value="<%=cBean.secondEmail%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Billing Contact</td>
		<td><input name="billingContact" type="text" class="forms"
			size="20" value="<%=cBean.billingContact%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Billing Phone</td>
		<td><input name="billingPhone" type="text" class="forms"
			size="15" value="<%=cBean.billingPhone%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Billing Email</td>
		<td><input name="billingEmail" type="text" class="forms"
			size="15" value="<%=cBean.billingEmail%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Web URL</td>
		<td><input name="web_URL" type="text" class="forms" size="30"
			value="<%=aBean.web_URL%>"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Tax ID</td>
		<td class="redMain"><input name="taxID" type="text" class="forms"
			size="9" maxlength="9" value="<%=cBean.taxID%>"> *(only
		digits 0-9, no dashes)</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<td class="blueMain" align="right">Logo (jpg or gif)<%=cBean.getIsLogoFile()%></td>
	<td><input name="logo_file" type="FILE" class="forms" size="15"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Co. Brochure<%=cBean.getIsBrochureFile()%></td>
		<td><input name="brochure_file" type="FILE" class="forms"
			size="15"></td>
	</tr>
	<tr>
		<td></td>
		<td class="blueMain" align="left">(Allowed formats: pdf, doc,
		txt, jpg)</td>
	<tr>
		<td class="blueMain" align="right" valign="top">Description</td>
		<td><textarea name="description" cols="32" rows="6" class="forms"><%=cBean.description%></textarea></td>
	</tr>
</table>
</div>
<div style="float: left; padding-left: 20px; border-left: black solid 2px;">
<table width="0" border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td class="blueMain" align="right">Visible?</td>
		<td class="blueMain" align="left"><input name="active"
			type="radio" value="Y" <%=aBean.getActiveChecked()%>> Yes <input
			name="active" type="radio" value="N" <%=aBean.getNotActiveChecked()%>>
		No</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Username</td>
		<td><input name="username" type="text" class="forms" size="15"
			value="<%=aBean.username%>"> <%
 	if (permissions.hasPermission(OpPerms.SwitchUser)) {
 %><a class="blueMain" href="login.jsp?switchUser=<%=aBean.username%>">Switch
		User</a> <%
 	}
 %>
		</td>
	</tr>	
	<tr>
		<td class="blueMain" align="right">Password</td>
		<td><input name="password" type="text" class="forms" size="15"
			value="<%=aBean.password%>"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Created by:</td>
		<td class="redMain" align="left"><%=aBean.createdBy%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Date Created:</td>
		<td class="redMain" align="left"><%=aBean.dateCreated%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">First Login:</td>
		<td class="redMain" align="left"><%=cBean.getAccountDate()%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Last Login:</td>
		<td class="redMain" align="left"><%=aBean.lastLogin%></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Welcome Email:</td>
		<td class="redMain" align="left"><a
			target="_blank" href="send_welcome_email.jsp?i=<%=editID%>"
			onClick="return confirm('Are you sure you want to send a welcome email to <%=aBean.email%>?');">
		Send Welcome Email</a></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Email Confirmed:</td>
		<td class="redMain"><%=aBean.emailConfirmedDate%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right"><nobr>Only
		Certificates?</nobr></td>
		<td class="blueMain" align="left"><%=Inputs.getYesNoRadio("isOnlyCerts", "forms", cBean.isOnlyCerts)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Must Pay</td>
		<td class="blueMain" align="left"><%=Inputs.getYesNoRadio("mustPay", "blueMain", cBean.mustPay)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right"><nobr>Membership Date:</nobr></td>
		<td><input name="membershipDate" id="membershipDate" type="text"
			class="forms" size="10"
			value="<%=cBean.membershipDate%>">
			<a id="anchor_membershipDate" name="anchor_membershipDate" href="#"
				onclick="cal1.select($('membershipDate'),'anchor_membershipDate','M/d/yy'); return false;"
				><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
		</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Last Invoice:</td>
		<td class="blueMain">$<input name="billingAmount"
			id="billingAmount" type="text" class="forms" size="10"
			value="<%=cBean.billingAmount%>"> on <input
			name="lastInvoiceDate" id="lastInvoiceDate" type="text" class="forms"
			size="10"
			value="<%=cBean.lastInvoiceDate%>">
			<a id="anchor_lastInvoiceDate" name="anchor_lastInvoiceDate" href="#"
				onclick="cal1.select($('lastInvoiceDate'),'anchor_lastInvoiceDate','M/d/yy'); return false;"
				><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
		</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Last Payment:</td>
		<td class="blueMain">$<input name="lastPaymentAmount"
			id="lastPaymentAmount" type="text" class="forms" size="10"
			value="<%=cBean.lastPaymentAmount%>"> on <input
			name="lastPayment" id="lastPayment" type="text" class="forms"
			size="10"
			value="<%=cBean.lastPayment%>">
			<a id="anchor_lastPayment" name="anchor_lastPayment" href="#"
				onclick="cal1.select($('lastPayment'),'anchor_lastPayment','M/d/yy'); return false;"
				><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
		</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Payment Expires:</td>
		<td><input name="paymentExpires" id="paymentExpires" type="text"
			class="forms" size="10"
			value="<%=cBean.paymentExpires%>">
			<a id="anchor_paymentExpires" name="anchor_paymentExpires" href="#"
				onclick="cal1.select($('paymentExpires'),'anchor_paymentExpires','M/d/yy'); return false;"
				><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
		</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Billing Cycle:</td>
		<td class="blueMain"><input name="billingCycle" id="billingCycle"
			type="text" class="forms" size="10" value="<%=cBean.billingCycle%>">Yrs.</td>
	</tr>
	<tr>
		<td class="blueMain" align="right"><nobr>New Billing
		Amount:</nobr></td>
		<td class="redMain">$<%=cBean.newBillingAmount%></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Industry</td>
		<td><%=AccountBean.getIndustrySelect("industry", "forms", aBean.industry)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right" valign="top">Main Trade</td>
		<td><%=tBean.getTradesNameSelect("main_trade", "blueMain", cBean.main_trade)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right" valign="top">Risk Level</td>
		<td class="redMain"><%=Inputs.getRadioInputWithOptions("riskLevel", "blueMain", cBean.riskLevel,
							ContractorBean.RISK_LEVEL_VALUES_ARRAY, ContractorBean.RISK_LEVEL_ARRAY)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Requested by</td>
		<td><%=oBean.getRequestedBySelect("requestedByID", "blueMain", cBean.requestedByID)%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Paying Facilities:</td>
		<td class="redMain"><%=cBean.payingFacilities%></td>
	</tr>
</table>
</div>
<br clear="all" />
<input name="submit" type="submit" class="forms" value="Save Contractor" />
</form>
<div style="position: relative;bottom: 30px;left: 35%"><a href="accounts_edit_contractor.jsp?id=<%=editID%>&Remove=Yes">Delete Contractor</a></div>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
