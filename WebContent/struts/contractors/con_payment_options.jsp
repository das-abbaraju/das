<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script language="JavaScript">

function updateExpDate() {
	$('ccexp').value = $F('expMonth') + $F('expYear');
}
</script>
</head>
<body>
<s:if test="permissions.contractor && !contractor.activeB">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<div id="main">
	<div id="bodyholder">
		<div id="content">
			<div align="center">
				<a href="javascript: window.close();">Close Window</a> |
				<a href="?id=<s:property value="id" />">Refresh Window</a>
				</div>
				<s:include value="../actionMessages.jsp"></s:include>
				
				<fieldset class="form">
				<legend><span>Membership Details</span></legend>
				<ol>
					<li><label>Company Name:</label>
						<s:property value="contractor.name" />
					</li>
				<s:if test="contractor.newBillingAmount > 0">
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
				</s:if>
				<s:else>
					<li><label>Status:</label>no payment required</li>
				</s:else>
				</ol>
				</fieldset>
				
					
				
				<s:if test="paymentMethod.toString().equals('CreditCard')">
					<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php" onsubmit="updateExpDate();">
						<input type="hidden" name="redirect" value="<s:property value="requestString"/>?id=<s:property value="id"/>"/>
						<s:hidden name="hash"></s:hidden>
						<s:hidden name="key_id"></s:hidden>
						<s:hidden name="orderid"></s:hidden>
						<s:hidden name="amount"></s:hidden>
						<s:hidden name="time"></s:hidden>
						<s:hidden name="company"></s:hidden>
						<s:hidden name="customer_vault_id"></s:hidden>
						<s:if test="cc == null">
							<input type="hidden" name="customer_vault" value="add_customer"/>
						</s:if>
						<s:else>
							<input type="hidden" name="customer_vault" value="update_customer"/>
						</s:else>
					
						<s:if test="cc != null">
						<fieldset class="form">
						<legend><span>Existing Card</span></legend>
						<ol>
							<li><label>Type:</label>
								<s:property value="cc.cardType"/>
							</li>
							<li><label>Number:</label>
								<s:property value="cc.cardNumber"/>
							</li>
							<li><label>Expires:</label>
								<s:property value="cc.expirationDateFormatted"/>
							</li>
							<li><a href="?id=<s:property value="id"/>&button=delete" class="remove">Remove Card</a></li>
						</ol>
						</fieldset>
						</s:if>
					
						<fieldset class="form">
						<legend><span><s:if test="cc == null">Add</s:if><s:else>Replace</s:else>
						 Credit Card</span></legend>
						<ol>
							<li><label>Type:</label>
								<s:radio theme="pics" list="creditCardTypes" name="ccName"/>
							</li>
							<li><label>Number:</label>
								<s:textfield name="ccnumber" size="20" />
							</li>
							<li><label>Expiration Date:</label>
								<s:select id="expMonth" list="#{'01':'Jan','02':'Feb','03':'Mar','04':'Apr','05':'May','06':'Jun','07':'Jul','08':'Aug','09':'Sep','10':'Oct','11':'Nov','12':'Dec'}"></s:select>
								<s:select id="expYear" list="#{'09':2009,10:2010,11:2011,12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019}"></s:select>
								<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" />
							</li>
							<li>
							<div class="buttons">
								<button class="positive" name="button" type="submit" value="Submit">Submit</button>
							</div>
							</li>
						</ol>
						</fieldset>	
					</form>
				</s:if>
				<s:else>
					<fieldset class="form">
					<legend><span>Check</span></legend>
					<ol>
					</ol>
					</fieldset>	
				</s:else>
				<br clear="all" /><br/><br/>
			</div>
		</div>
	</div>
	<s:if test="permissions.contractor && !contractor.activeB">
		<div class="buttons" style="float: right;">
			<a href="contractor_new_confirm.jsp" class="positive">Next</a>
		</div>
	</s:if>
</body>
</html>
