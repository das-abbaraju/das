<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>
<h1><s:property value="contractor.name" /> <span class="sub"><s:property
	value="subHeading" /></span></h1>



<s:form id="save">
<table>
	<tr>
		<td style="vertical-align: top">
			<table class="forms">
				<tr>
					<th>Name:</th>
					<td><s:textfield name="contractor.name" size="50" /></td>
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
					<td><s:textfield name="contractor.web_URL" size="50" /></td>
				</tr>
				<tr>
					<th>Tax ID:</th>
					<td><s:textfield name="contractor.taxId" size="9" maxLength="9"/>*(only	digits 0-9, no dashes)</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>	
				<tr>
					<th>Company Logo:</th>
					<td><s:file name="contractor.logoFile" size="15" /></td>
				</tr>
				<tr>
					<td></td>
					<td>(Allowed formats: jpg, gif)</td>
				</tr>	
				<tr>
					<th>Company Brochure:</th>
					<td><s:file name="contractor.brochureFile" size="15" /></td>
				</tr>	
				<tr>
					<td></td>
					<td>(Allowed formats: pdf, doc, txt, jpg)</td>
				</tr>	
				<tr>
					<th>Description:</th>
					<td><s:textarea name="contractor.description" cols="32" rows="6"/></td>
				</tr>				
			</table>
			<div class="buttons">
				<button class="positive" name="button" type="submit" value="save">Save</button>
				<button name="button" type="submit" value="delete">Delete</button>
			</div>
		</td>
		<td style="vertical-align: top">
			<table class="forms">
				<tr>
					<th>Visible?</th>
					<td><s:radio list="#{'Yes':'Yes','No':'No'}" name="active" value="active"></s:radio></td>
				</tr>
				<tr>
					<th>Username:</th>
					<td><s:textfield name="contractor.username" size="45" /></td>
				</tr>
				<tr>
					<th>Password:</th>
					<td><s:textfield name="contractor.password" size="45" /></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th>Created by:</th>
					<td><s:textfield name="contractor.createdBy" size="45" /></td>
				</tr>
				<tr>
					<th>Date Created:</th>
					<td><s:date name="contractor.dateCreated" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>First Login:</th>
					<td><s:date name="contractor.getAccountDate" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Last Login:</th>
					<td><s:date name="contractor.lastLogin" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>				
				<tr>
					<th>Welcome Email:</th>
					<td>
						<a target="_blank" href="send_welcome_email.jsp?id=<s:property value="id"/>"
							onClick="return confirm('Are you sure you want to send a welcome email to <s:property value="contractor.name"/>?');">Send Welcome Email</a>
					</td>
				</tr>
				<tr>
					<th>Email Confirmed:</th>
					<td><s:date name="contractor.emailConfirmedDate" format="MMM d, yyyy"/></td>
				</tr>				
				<tr>
					<th>Only Certificates?</th>
					<td><s:radio list="#{'Yes':'Yes','No':'No'}" name="isOnlyCerts" value="contractor.isOnlyCerts"></s:radio></td>
				</tr>				
				<tr>
					<th>Must Pay?</th>
					<td><s:radio list="#{'Yes':'Yes','No':'No'}" name="mustPay" value="contractor.mustPay"></s:radio></td>
				</tr>				
				<tr>
					<th>Membership Date:</th>
					<td><s:date name="contractor.membershipDate" format="MMM d, yyyy" /></td>
				</tr>	
				<tr>
					<th>Last Invoice:</th>
					<td>$<s:textfield name="contractor.billingAmount" size="10"/></td>
				</tr>	
				<tr>
					<th>Last Payment:</th>
					<td>$<s:textfield name="contractor.lastPaymentAmount" size="10"/></td>
				</tr>
				<tr>
					<th>Payment Expires:</th>
					<td><s:date name="contractor.paymentExpires" format="MMM d, yyyy" /></td>
				</tr>
				<tr>
					<th>Billing Cycle:</th>
					<td><s:textfield name="contractor.billingCycle" size="10"/>Yrs.</td>
				</tr>							
				<tr>
					<th>New Billing	Amount:</th>
					<td>$<s:textfield name="contractor.newBillingAmount" size="10"/></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<th>Industry:</th>
					<td><s:textfield name="contractor.industry" size="20"/></td>
				</tr>								
				<tr>
					<th>Main Trade:</th>
					<td><s:textfield name="contractor.main_trade" size="20"/></td>
				</tr>					
				<tr>
					<th>Risk Level:</th>
					<td><s:radio list="#{'Low':'Low','Med':'Med','High':'High'}" name="riskLevel" value="contractor.riskLevel"></s:radio></td>
				</tr>				
				<tr>
					<th>Requested By:</th>
					<td><s:textfield name="contractor.requestedByID.name" size="20"/></td>
				</tr>				
				<tr>
					<th>Paying Facilities:</th>
					<td><s:property value="contractor.payingFacilities" size="20"/></td>
				</tr>					
																				
			</table>
		</td>		
	</tr>
</table>
</s:form>
</body>
</html>
