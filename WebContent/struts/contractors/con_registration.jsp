<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Contractor Registration</title>
<meta name="color" content="#669966" />
<meta name="flashName" content="REGISTER" />
<meta name="iconName" content="register" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />	
<script language="JavaScript" SRC="js/prototype.js"></script>
<script language="Javascript">	
function checkUsername(username) {
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID=0&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}

function checkTaxId(taxId) {
	$('taxId_status').innerHTML = 'checking availability of taxId...';
	pars = 'taxId='+taxId;
	var myAjax = new Ajax.Updater('taxId_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}
	
</script>	
</head>
<body>
<s:include value="registrationHeader.jsp"></s:include>
<span class="redMain">* - Indicates required information</span>
<s:form method="POST">
	<br clear="all" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
					<legend><span>Details</span></legend>
					<ol>
						<li><label>Company Name:</label>
							<s:textfield name="contractor.name" size="35" /><span class="redMain">*</span></li>
						<li><label>Contact:</label>
							<s:textfield name="contractor.contact" size="35" /><span class="redMain">*</span></li>
						<li><label>Web URL:</label> 
							<s:textfield name="contractor.webUrl" size="35" />Example: www.site.com</li>
						<li><label>Tax ID:</label> <s:textfield name="contractor.taxId"
							size="9" maxLength="9" onblur="checkTaxId(this.value);" />
							<span id="taxId_status"></span><span class="redMain">* Only digits 0-9, no dashes</span>
							</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Primary Address</span></legend>
					<ol>
						<li><label>Address:</label>
							<s:textfield name="contractor.address" size="35" /><span class="redMain">*</span></li>
						<li><label>City:</label> 
							<s:textfield name="contractor.city" size="35" /><span class="redMain">*</span></li>
						<li><label>State/Province:</label>
							<s:select list="StateList" name="contractor.state"/><span class="redMain">*</span></li>
						<li><label>Zip:</label>
							<s:textfield name="contractor.zip" size="35" /><span class="redMain">*</span></li>
						<li><label>Phone:</label>
							<s:textfield name="contractor.phone" size="35" /><span class="redMain">*</span></li>
						<li><label>Phone 2:</label> 
							<s:textfield name="contractor.phone2" size="35" /></li>
						<li><label>Fax:</label> 
							<s:textfield name="contractor.fax" size="35" /></li>
						<li><label>Email:</label> 
							<s:textfield name="contractor.email" size="35" /><span class="redMain">* We send vital account info to this email</span></li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Secondary Address</span></legend>
					<ol>
						<li><label>Second Contact:</label>
							<s:textfield name="contractor.secondContact" size="35" /></li>
						<li><label>Second Phone:</label>
							<s:textfield name="contractor.secondPhone" size="35" /></li>
						<li><label>Second Email:</label>
							<s:textfield name="contractor.secondEmail" size="35" /></li>
						<li><label>Billing Contact:</label> 
							<s:textfield name="contractor.billingContact" size="35" /></li>
						<li><label>Billing Phone:</label>
							<s:textfield name="contractor.billingPhone" size="35" /></li>
						<li><label>Billing Email:</label>
							<s:textfield name="contractor.billingEmail" size="35" /></li>
					</ol>
					</fieldset>
					<fieldset class="form">
						<legend><span>Industry Details</span></legend>
			<ol>
				<li><label>Industry:</label> <s:select list="industryList"
					name="contractor.industry" />
				<li><label>Main Trade:</label> <s:select list="tradeList"
					name="contractor.mainTrade" headerKey=""
					headerValue="- Choose a trade -" listKey="question"
					listValue="question" /><span class="redMain">*</span></li>
				<li><label>Requested By:</label>
						<s:select cssStyle="font-size: 12px;" list="operatorList" name="contractor.requestedById" headerKey="0" headerValue="- Choose an operator -" listKey="id" listValue="name"/>
				</li>
				<li><label>DOT OQ:</label> 
					Does your company have employees who are covered under DOT OQ requirements?
					<br />
					<s:radio list="#{'Yes':'Yes','No':'No'}"
						name="contractor.oqEmployees" theme="pics"/></li>
				<li><label>Risk Level:<span class="redMain">*</span></label>
				<table class="report">
					<tr><td>
					<s:radio theme="pics" list="#{'Low':'Low</td><td><nobr>Delivery,
						janitorial, off site engineering, security, computer services,
						etc.</nobr></td></tr><tr><td>',
						'Med':'Med</td><td>On site engineering, safety services, landscaping, inspection services,etc.</td></tr><tr><td>',
						'High':'High</td><td>Mechanical contractor, remediation, industrial cleaning, general construction, etc.'}" name="contractor.riskLevel"></s:radio>
					</td></tr>	
				</table>
			</ol>
			</fieldset>
					<fieldset class="form">
						<legend><span>Company Identification</span></legend>
						<ol>
							<li><label>Description:</label>
								<s:textarea name="contractor.description" cols="40" rows="15" />
								<br/>Include up to 2000 words to describe your company. 
                        		<br>
                        		<span class="blueMain">Suggestion:</span> copy and paste text from the &quot;about&quot; section 
                      			on your website or company brochure.
							</li>
						</ol>
					</fieldset>
					<fieldset class="form"><legend><span>Login Information</span></legend>
						<ol>
							<li><label>Username:</label>
						 		<s:textfield name="user.username" onblur="checkUsername(this.value);"/>
						 		<span id="username_status"></span><span class="redMain">* Please type in your desired user name</span>
						 	</li>
							<li><label>Password:</label> 
								<s:password name="user.password"/>
								<span class="redMain">* At least 5 characters long and different from your username</span></li>
							<li><label>Confirm Password:</label> 
								<s:password name="confirmPassword"/>
							</li>
						</ol>
					</fieldset>
					<fieldset class="form submit">
						<div class="buttons">
							<button class="positive" value="Register" name="button" type="submit">Register</button>
						</div>
					</fieldset>
			</td>
		</tr>
	</table>
</s:form>
</body>
</html>
