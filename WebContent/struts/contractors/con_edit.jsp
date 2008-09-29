<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script language="JavaScript" SRC="js/CalendarPopup.js"></script>
<script language="JavaScript">
	var cal1 = new CalendarPopup('caldiv1');
	cal1.offsetY = -110;
	cal1.setCssPrefix("PICS");
</script>
</head>
<body>

<s:include value="conHeader.jsp"></s:include>


<s:form id="save" method="POST" enctype="multipart/form-data">
	<s:hidden name="id" />
	<div class="buttons">
	<button class="positive" name="button" type="submit" value="save">Save</button>
	</div>
	<br clear="all" />
	<table>
		<tr>
			<td style="vertical-align: top">
			<table class="forms">
				<tr>
					<th>Name:</th>
					<td><s:textfield name="contractor.name" size="50" /></td>
				</tr>
				<tr>
					<th>Username:</th>
					<td><s:textfield name="contractor.username" size="35" />
						<pics:permission perm="SwitchUser">
							<a href="login.jsp?switchUser=<s:property value="contractor.username"/>">Switch User</a>							
						</pics:permission>
					</td>
				</tr>
				<tr>
					<th>Password:</th>
					<td><s:textfield name="contractor.password" size="45" /></td>
				</tr>
				<tr>
					<th>Date Created:</th>
					<td><s:date name="contractor.dateCreated" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>First Login:</th>
					<td><s:date name="contractor.getAccountDate"
						format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Last Login:</th>
					<td><s:date name="contractor.lastLogin" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<th>Address:</th>
					<td><s:textfield name="contractor.address" size="45" /></td>
				</tr>
				<tr>
					<th>City:</th>
					<td><s:textfield name="contractor.city" size="20" /></td>
				</tr>
				<tr>
					<th>State/Province:</th>
					<td><s:textfield name="contractor.state" size="50" /></td>
				</tr>
				<tr>
					<th>Zip:</th>
					<td><s:textfield name="contractor.zip" size="7" /></td>
				</tr>
				<tr>
					<th>Phone:</th>
					<td><s:textfield name="contractor.phone" size="15" /></td>
				</tr>
				<tr>
					<th>Phone 2:</th>
					<td><s:textfield name="contractor.phone2" size="15" /></td>
				</tr>
				<tr>
					<th>Fax:</th>
					<td><s:textfield name="contractor.fax" size="15" /></td>
				</tr>
				<tr>
					<th>Email:</th>
					<td><s:textfield name="contractor.email" size="30" /></td>
				</tr>
				<tr>
					<th>Email Confirmed Date:</th>
					<td><s:date name="contractor.emailConfirmedDate" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Contact:</th>
					<td><s:textfield name="contractor.contact" size="20" /></td>
				</tr>
				<tr>
					<th>Second Contact:</th>
					<td><s:textfield name="contractor.secondContact" size="20" /></td>
				</tr>
				<tr>
					<th>Second Phone:</th>
					<td><s:textfield name="contractor.secondPhone" size="50" /></td>
				</tr>
				<tr>
					<th>Second Email:</th>
					<td><s:textfield name="contractor.secondEmail" size="50" /></td>
				</tr>
				<tr>
					<th>Billing Contact:</th>
					<td><s:textfield name="contractor.billingContact" size="50" /></td>
				</tr>
				<tr>
					<th>Billing Phone:</th>
					<td><s:textfield name="contractor.billingPhone" size="50" /></td>
				</tr>
				<tr>
					<th>Billing Email:</th>
					<td><s:textfield name="contractor.billingEmail" size="50" /></td>
				</tr>
				<tr>
					<th>Web URL:</th>
					<td><s:textfield name="contractor.webUrl" size="50" /></td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<th>Tax ID:</th>
					<td><s:textfield name="contractor.taxId" size="9"
						maxLength="9" />*(only digits 0-9, no dashes)</td>
				</tr>
				<tr>
					<th>Industry:</th>
					<td><s:select list="industryList" name="contractor.industry"/></td>
				</tr>
				<tr>
					<th>Main Trade:</th>
					<td><s:select list="tradeList" name="contractor.mainTrade" headerKey="" headerValue="- Choose a trade -" listKey="question" listValue="question"/></td>
				</tr>
				<tr>
					<th>Risk Level:</th>
					<td><s:radio list="riskLevelList" name="contractor.riskLevel"/></td>
				</tr>
				<tr>
					<th>Requested By:</th>
					<td><s:select list="operatorList" name="contractor.requestedById" headerKey="0" headerValue="- Choose an operator -" listKey="id" listValue="name"/></td>
				</tr>
				<tr>
					<th>Paying Facilities:</th>
					<td><s:property value="contractor.payingFacilities" /></td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<th>Membership Date:</th>
					<td><s:date name="contractor.membershipDate" format="MMM d, yyyy" /></td>

				</tr>
				<tr>
					<th>Last Invoice:</th>
					<td>$<s:property value="contractor.billingAmount" /> on <s:date name="contractor.lastInvoiceDate" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Last Payment:</th>
					<td>$<s:property value="contractor.lastPaymentAmount" /> on <s:date name="contractor.lastPayment" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Payment Expires:</th>
					<td><s:date name="contractor.paymentExpires" format="MMM d, yyyy" /></td>
				</tr>				
				<tr>
					<th>Billing Cycle:</th>
					<td><s:property value="contractor.billingCycle" /> year(s)</td>
				</tr>
				<tr>
					<th>New Billing Amount:</th>
					<td>$<s:property value="contractor.newBillingAmount" /></td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<th>Company Logo:</th>
					<td><s:file name="logo" size="35" /></td>
				</tr>
				<tr>
					<td></td>
					<td>(Allowed formats: jpg, gif, png)</td>
				</tr>
				<tr>
					<th>Company Brochure:</th>
					<td><s:file name="brochure" size="35" /></td>
				</tr>
				<tr>
					<td></td>
					<td>(Allowed formats: pdf, doc, jpg, gif, png)</td>
				</tr>
				<tr>
					<th>Description:</th>
					<td><s:textarea name="contractor.description" cols="40"
						rows="15" /></td>
				</tr>
			</table>
			</td>
			<s:if test="permissions.admin">
				<td style="vertical-align: top">
				<h2>PICS Admin Fields</h2>
				<table class="forms">
					<tr>
						<th>Visible?</th>
						<td><s:radio list="#{'Y':'Yes','N':'No'}" name="contractor.active" /></td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<th>Created by:</th>
						<td><s:textfield name="contractor.createdBy" size="45" /></td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<th>Welcome Email:</th>
						<td><a target="_blank"
							href="send_welcome_email.jsp?id=<s:property value="id"/>"
							onClick="return confirm('Are you sure you want to send a welcome email to <s:property value="contractor.name"/>?');">Send
						Welcome Email</a></td>
					</tr>
					<tr>
						<th>Only Certificates?</th>
						<td><s:radio list="#{'Yes':'Yes','No':'No'}"
							name="contractor.isOnlyCerts" value="contractor.isOnlyCerts"/></td>
					</tr>
					<tr>
						<th>Must Pay?</th>
						<td><s:radio list="#{'Yes':'Yes','No':'No'}" name="contractor.mustPay"
							value="contractor.mustPay"/></td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
				<tr>
					<th>Membership Date:</th>
					<td>
						<input name="contractor.membershipDate" id="membershipDate" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.membershipDate" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchormembershipDate" name="anchormembershipDate" 
							onclick="cal1.select($('membershipDate'), 'anchormembershipDate','MM/dd/yyyy'); return false;">
							<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</td>
				</tr>
				<tr>
					<th>Last Invoice:</th>
					<td>$<s:textfield name="contractor.billingAmount" size="6" /> on 
							<input name="contractor.lastInvoiceDate" id="lastInvoiceDate" 
								type="text" class="forms" size="10" 
								value="<s:date name="contractor.lastInvoiceDate" format="MM/dd/yyyy" />" />
							<a href="#" 
								id="anchorlastInvoiceDate" name="anchorlastInvoiceDate" 
								onclick="cal1.select($('lastInvoiceDate'), 'anchorlastInvoiceDate','MM/dd/yyyy'); return false;">
								<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</td>								
				</tr>
				<tr>
					<th>Last Payment:</th>
					<td>$<s:textfield name="contractor.lastPaymentAmount" size="6" /> on 
							<input name="contractor.lastPayment" id="lastPayment" 
								type="text" class="forms" size="10" 
								value="<s:date name="contractor.lastPayment" format="MM/dd/yyyy" />" />
							<a href="#" 
								id="anchorlastPayment" name="anchorlastPayment" 
								onclick="cal1.select($('lastPayment'), 'anchorlastPayment','MM/dd/yyyy'); return false;">
								<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</td>
				</tr>
				<tr>
					<th>Payment Expires:</th>
					<td>						
						<input name="contractor.paymentExpires" id="paymentExpires" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.paymentExpires" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchorpaymentExpires" name="anchorpaymentExpires" 
							onclick="cal1.select($('paymentExpires'), 'anchorpaymentExpires','MM/dd/yyyy'); return false;">
							<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</td>							
				</tr>				
				<tr>
					<th>Billing Cycle:</th>
					<td><s:textfield name="contractor.billingCycle" size="1" /> year(s)</td>
				</tr>
				<tr>
					<th>New Billing Amount:</th>
					<td>$<s:textfield name="contractor.newBillingAmount" size="6" /></td>
				</tr>					
				</table>
				</td>
			</s:if>
		</tr>
	</table>
	<div class="buttons">
	<pics:permission perm="ContractorAccounts" type="Edit">
		<button class="positive" name="button" type="submit" value="save">Save</button>
	</pics:permission>
	<pics:permission perm="RemoveContractors">
		<button name="button" type="submit" value="delete" onClick="return confirm('Are you sure you want to delete this account?');">Delete</button>
	</pics:permission>
	</div>
</s:form>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
