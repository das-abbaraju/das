<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.util.URLUtils"%>
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

	function showPaymentOptions(conId, method) {
 		var buttonURL= window.location.href;
 		
 		if( buttonURL.indexOf( 'www.picsauditing.com' ) != -1 ) {
 			buttonURL = buttonURL.replace('http:', 'https:');
 		}
 		var url = buttonURL.substr(0, buttonURL.lastIndexOf('/') ) + '/ContractorPaymentOptions.action?id=' + conId +'&paymentMethod=' + method; 
		title = 'Contractor Payment Options';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=600,toolbar=1,directories=0,menubar=0';
		popupWindow = window.open(url,title,pars);
		popupWindow.focus();
		return false;
	}
	
	function setPaymentUrl() {
 		var buttonURL= window.location.href;

 		if( buttonURL.indexOf( 'www.picsauditing.com' ) != -1 ) {
 			buttonURL = buttonURL.replace('http:', 'https:');
 		}
 		var url = buttonURL.substr(0, buttonURL.lastIndexOf('/') ) + '/ContractorPaymentOptions.action?id=' + <s:property value="contractor.id"/> +'&paymentMethod=Credit Card'; 
		$('cc_link').href=url;
	}
	
	function showPaymentMethodOption(elm) {
		var option =  $F(elm);
		if(option == 'Check') {
			$('creditcard_show').hide();
			$('check_show').show();
		}
		if(option == 'Credit Card') {
			$('check_show').hide();
			$('creditcard_show').show();
		}
		return false;
	}
</script>
</head>
<body onload="javascript: setPaymentUrl();">

<s:if test="permissions.active">
	<s:include value="conHeader.jsp"></s:include>
</s:if>
<s:else><h1><s:property value="contractor.name" /></h1></s:else>
<s:form id="save" method="POST" enctype="multipart/form-data">
<div class="buttons">
	<!-- <button class="positive" name="button" type="submit" value="Save">Save</button>  -->
	<input type="submit" class="positive" name="button" value="Save"/>
</div>
<br clear="all" />
<s:hidden name="id" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
				<legend><span>Details</span></legend>
				<ol>
					<li><label>Name:</label>
						<s:textfield name="contractor.name" size="35" />
					</li>
					<li><label>Username:</label>
						<s:textfield name="contractor.username" size="20" />
							<pics:permission perm="SwitchUser">
								<a href="login.jsp?switchUser=<s:property value="contractor.username"/>">Switch User</a>							
							</pics:permission>
					</li>
					<li><label>Change Password:</label>
						<s:password name="password1" size="15" />
					</li>
					<li><label>Confirm Password:</label>
						<s:password name="password2" size="15" />
					</li>
					<li><label>Date Created:</label>
						<s:date name="contractor.dateCreated" format="MMM d, yyyy" />
					</li>
					<li><label>First Login:</label>
						<s:date name="contractor.accountDate" format="MMM d, yyyy" />
					</li>
					<li><label>Last Login:</label>
						<s:date name="contractor.lastLogin" format="MMM d, yyyy" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Billing Details</span></legend>
				<ol>
				<s:if test="permissions.contractor">
				<li><div id="info">
					<s:if test="contractor.activeB">
						As an improvement, you may now pay by credit card.  Even though you are providing your credit card information at this time, your card will not be charged until the next billing date.  PICS will email you 7 days prior to renewal before any charges are applied.  If you have questions, contact PICS Accounting any time at (800) 506-7427 x 2.
					</s:if>
					<s:else>
						Please enter your credit card information, which will expedite the registration process.  Your membership is valid for 12 months from the charge date.  An upgrade fee will be charged only if you add additional facilities to your account.  PICS will email you 7 days prior to any charge. If you have questions, contact PICS Accounting anytime at (800) 506-7427 x 2.
					</s:else>
					</div>
				</li>
				</s:if>
				<s:if test="contractor.activeB">
					<li><label>Next Billing Date:</label> <s:date
						name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
					<li><label>Next Billing Amount:</label> $<s:property
						value="contractor.newBillingAmount" /> USD</li>
				</s:if>
				<s:else>
					<li><label>Membership Fee:</label> $<s:property
						value="contractor.newBillingAmount" /> USD</li>
					<li><label>Activation Fee:</label> $<s:property value="contractor.activationFee"/> USD</li>
					<li><label>Total:</label> $<s:property value="contractor.activationFee+contractor.newBillingAmount"/> USD </li>
				</s:else>
				<li><label>Payment Method:</label>
					<s:if test="contractor.newBillingAmount < 500 && !permissions.admin">
						<s:radio list="#{'Check':'Check','Credit Card':'Credit Card'}" name="contractor.paymentMethod" theme="pics" disabled="true"/>
					</s:if>
					<s:else>
						<s:radio list="#{'Check':'Check','Credit Card':'Credit Card'}" name="contractor.paymentMethod" theme="pics" onclick="javascript : showPaymentMethodOption(this); return true;"/>
					</s:else>
				</li>
				<li>
					<s:if test="contractor.paymentMethodCreditCard">
						<s:set name="creditcard_show" value="'inline'"/>								
						<s:set name="check_show" value="'none'"/>
					</s:if>
					<s:else>
						<s:set name="creditcard_show" value="'none'"/>								
						<s:set name="check_show" value="'inline'"/>
					</s:else>

					<span id="creditcard_show" style="display: <s:property value="#attr.creditcard_show"/>;"> Credit card payment is required for billing amounts less than $500. <a id="cc_link" href="#" target="_BLANK" title="Opens In a Secure Window">Edit Credit Card</a><br/></span>
					<span id="check_show" style="display: <s:property value="#attr.check_show"/>;"> Your invoice will be generated on <s:date name="@com.picsauditing.PICS.DateBean@getFirstofMonth(contractor.paymentExpires,-1)" format="MMM d, yyyy"/> and emailed to <s:property value="contractor.email"/> 
					<s:if test="!@com.picsauditing.util.Strings@isEmpty(contractor.billingEmail) && !contractor.email.equals(contractor.billingEmail)">	and <s:property value="contractor.billingEmail"/></s:if> with payment terms of net 30.</span>
				</li>
				<li><label>&nbsp;</label>
						<a href="privacy_policy.jsp">Privacy Policy</a> 
						| <a href="refund_policy.jsp">Refund Policy</a>
				</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Primary Address</span></legend>
				<ol>
					<li><label>Address:</label>
						<s:textfield name="contractor.address" size="35" />
					</li>
					<li><label>City:</label>
						<s:textfield name="contractor.city" size="20" />
					</li>
					<li><label>State/Province:</label>
						<s:textfield name="contractor.state" size="5" />
					</li>
					<li><label>Zip:</label>
						<s:textfield name="contractor.zip" size="7" />
					</li>
					<li><label>Phone:</label>
						<s:textfield name="contractor.phone" size="15" />
					</li>
					<li><label>Phone 2:</label>
						<s:textfield name="contractor.phone2" size="15" />
					</li>
					<li><label>Fax:</label>
						<s:textfield name="contractor.fax" size="15" />
					</li>
					<li><label>Email:</label>
						<s:textfield name="contractor.email" size="30" />
					</li>
					<li><label>Email Confirmed Date:</label>
						<s:date name="contractor.emailConfirmedDate" format="MMM d, yyyy" />
					</li>
					<li><label>Contact:</label>
						<s:textfield name="contractor.contact" size="20" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Secondary Address</span></legend>
				<ol>		
					<li><label>Second Contact:</label>
						<s:textfield name="contractor.secondContact" size="20" />
					</li>
					<li><label>Second Phone:</label>
						<s:textfield name="contractor.secondPhone" size="15" />
					</li>
					<li><label>Second Email:</label>
						<s:textfield name="contractor.secondEmail" size="30" />
					</li>
					<li><label>Billing Contact:</label>
						<s:textfield name="contractor.billingContact" size="20" />
					</li>
					<li><label>Billing Phone:</label>
						<s:textfield name="contractor.billingPhone" size="15" />
					</li>
					<li><label>Billing Email:</label>
						<s:textfield name="contractor.billingEmail" size="30" />
					</li>
					<li><label>Web URL:</label>
						<s:textfield name="contractor.webUrl" size="30" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Industry Details</span></legend>
				<ol>
					<li><label>Tax ID:</label>
						<s:textfield name="contractor.taxId" size="9" maxLength="9" />*(only digits 0-9, no dashes)
					</li>
					<li><label>Industry:</label>
						<s:select list="industryList" name="contractor.industry"/>
					</li>
					<li><label>Main Trade:</label>
						<s:select cssStyle="font-size: 12px;" list="tradeList" name="contractor.mainTrade" headerKey="" headerValue="- Choose a trade -" listKey="question" listValue="question"/>
					</li>
					<li><label>Risk Level:</label>
						<s:property value="contractor.riskLevel"/>
					</li>
					<li><label>Requested By:</label>
						<s:select cssStyle="font-size: 12px;" list="operatorList" name="contractor.requestedById" headerKey="0" headerValue="- Choose an operator -" listKey="id" listValue="name"/>
					</li>
					<li><label>Paying Facilities:</label>
						<s:property value="contractor.payingFacilities" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Company Identification</span></legend>
				<ol>
					<li><label>Company Logo:</label>
						<s:file name="logo" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: jpg, gif, png)
					</li>
					<li><label>Company Brochure:</label>
						<s:file name="brochure" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: pdf, doc, jpg, gif, png)
					</li>
					<li><label>Description:</label>
						<s:textarea name="contractor.description" cols="40"	rows="15" />
					</li>	
				</ol>
				</fieldset>
			</td>
		<s:if test="permissions.admin">
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form">
				<legend><span>PICS Admin Fields</span></legend>
				<ol>
					<li><label>Visible?</label>
						<s:radio list="#{'Y':'Yes','N':'No'}" name="contractor.active" theme="pics" />
					</li>
					<li><label>Risk Level:</label>
						<s:radio list="riskLevelList" name="contractor.riskLevel" theme="pics" />
					</li>
					<li><label>Created by:</label>
						<s:textfield name="contractor.createdBy" size="25" />
					</li>
					<li><label>Password:</label>
						<s:property value="contractor.password" />
						<br />* We will eventually hide this field and allow you to reset the password instead.
					</li>
					<li><label>Welcome Email:</label>
						<a target="_blank"
							href="send_welcome_email.jsp?id=<s:property value="id"/>"
							onClick="return confirm('Are you sure you want to send a welcome email to <s:property value="contractor.name"/>?');">Send
							Welcome Email</a>
					</li>
					<li><label>Only Certificates?</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}"
							name="contractor.isOnlyCerts" value="contractor.isOnlyCerts" theme="pics" />
					</li>
					<li><label>Must Pay?</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="contractor.mustPay"
							value="contractor.mustPay" theme="pics" />
					</li>
					<li><label>Membership Date:</label>
						<input name="contractor.membershipDate" id="membershipDate" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.membershipDate" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchormembershipDate" name="anchormembershipDate" 
							onclick="cal1.select($('membershipDate'), 'anchormembershipDate','MM/dd/yyyy'); return false;">
							<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</li>
					<li><label>Last Invoice:</label>
						USD <s:textfield name="contractor.billingAmount" size="6" /> on 
						<input name="contractor.lastInvoiceDate" id="lastInvoiceDate" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.lastInvoiceDate" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchorlastInvoiceDate" name="anchorlastInvoiceDate" 
							onclick="cal1.select($('lastInvoiceDate'), 'anchorlastInvoiceDate','MM/dd/yyyy'); return false;">
							<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</li>
					<li><label>Last Payment:</label>
						USD <s:textfield name="contractor.lastPaymentAmount" size="6" /> on 
						<input name="contractor.lastPayment" id="lastPayment" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.lastPayment" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchorlastPayment" name="anchorlastPayment" 
							onclick="cal1.select($('lastPayment'), 'anchorlastPayment','MM/dd/yyyy'); return false;">
							<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</li>
					<li><label>Next Billing Date:</label>
						<input name="contractor.paymentExpires" id="paymentExpires" 
							type="text" class="forms" size="10" 
							value="<s:date name="contractor.paymentExpires" format="MM/dd/yyyy" />" />
						<a href="#" 
							id="anchorpaymentExpires" name="anchorpaymentExpires" 
							onclick="cal1.select($('paymentExpires'), 'anchorpaymentExpires','MM/dd/yyyy'); return false;">
						<img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					</li>
					<li><label>Next Billing Amount:</label>
						USD <s:property value="contractor.newBillingAmount" />
					</li>
				</ol>
				</fieldset>
			</td>
		</s:if>			
		</tr>
	</table>
<br clear="all">
	<div class="buttons">
		<s:if test="permissions.contractor">
<!-- 			<button class="positive" name="button" type="submit" value="Save">Save</button>  -->
			<input type="submit" class="positive" name="button" value="Save"/>
		</s:if>
		<s:else>
			<pics:permission perm="ContractorAccounts" type="Edit">
				<!-- <button class="positive" name="button" type="submit" value="Save">Save</button> -->
				<input type="submit" class="positive" name="button" value="Save"/>
			</pics:permission>
		</s:else>
		<pics:permission perm="RemoveContractors">
			<!-- <button name="button" class="negative" type="submit" value="Delete" onClick="return confirm('Are you sure you want to delete this account?');">Delete</button> -->
			<input type="submit" class="negative" name="button" value="Delete" onClick="return confirm('Are you sure you want to delete this account?');"/>
		</pics:permission>
	</div>
</s:form>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
