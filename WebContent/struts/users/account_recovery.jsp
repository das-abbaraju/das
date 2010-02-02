<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
<title>Login</title>
<meta name="accountrecovery" content="Account_Recovery">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<style>
fieldset.form label {
	float: none;
	width: 10em;
	margin-right: 1em;
	text-align: right;
	font-size: 15px;
	font-weight: bold;
	color: #003768;
	line-height: 15px;
}
fieldset.form {
	margin-top: 0pt;
	margin-right: 0pt;
	margin-bottom: 0pt;
	margin-left: 0pt;
	padding-top: 7px;
	padding-right: 7px;
	padding-bottom: 7px;
	padding-left: 7px;
	border-right-style-value: none;
	border-bottom-style: none;
	border-left-style-value: none;
	border-left-style-ltr-source: physical;
	border-left-style-rtl-source: physical;
	border-right-style-ltr-source: physical;
	border-right-style-rtl-source: physical;
	border-width: 2px;
	border-style: solid;
	border-color: #c3c3c3;
	background-color: #f6f6f0;
	width: 100%;
	float: left;
	clear: both;
	position: relative;
	color: #404245;
}
</style>
<s:include value="../jquery.jsp"/>
<script>

var RecaptchaOptions = {
   theme : 'white'
};

function showUsernameData() {
	$('#emailBox').val('');
	$('.usernameButton').show();
	$('.emailButton').hide();
	$('.usernameMessage').show();
	$('.emailMessage').hide();
}

function showEmailData() {
	$('#usernameBox').val('');
	$('.usernameButton').hide();
	$('.emailButton').show();
	$('.usernameMessage').hide();
	$('.emailMessage').show();
}

$(function() {
	showUsernameData();
});

</script>
</head>
<body>

<s:include value="../actionMessages.jsp"></s:include>
<s:form cssStyle="width: 450px;float:left;margin:0px 20px 10px 0;">
	<h1>Account Recovery</h1>
	<div style="margin-bottom: 10px;">
		<a class="picsbutton" href="Login.action">&lt;&lt; Return to Login Page</a>
	</div>
	<fieldset class="form">
	<ol style="margin-top:7px;">
		<li>
			<table>
				<tr>
				<td>
					<div><label>Username:<br/></label> <s:textfield id="usernameBox" name="username" cssClass="login" onchange="showUsernameData()" onclick="showUsernameData()"/></div>
				</td>
				<td>
					<br/>&nbsp;&nbsp;&nbsp;or&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				<td>
					<div><label>Email:</label><br/><s:textfield id="emailBox" name="email" cssClass="login" size="28" onchange="showEmailData()" onclick="showEmailData()"/></div>
				</td>
				</tr>
			</table>		
		</li>
			
		<li>
			<div class="usernameMessage">Enter your username and we will send you an email containing a link that you can use to reset the password on your account. If you have any problems, please check your spam filters or <a href="Contact.action" title="Contact PICS">contact us</a> directly.</div>
			<div class="emailMessage">Enter your email address to receive the username(s) associated with that address. If you have any problems, please check your spam filters or <a href="Contact.action" title="Contact PICS">contact us</a> directly.</div>
		</li>
		<li>
			<div style="margin-left: 7px;">
				<label>Enter Verification:</label><br/>
				<s:property value="recaptcha.recaptchaHtml" escape="false"/>
			</div>
		</li>
		<li>
		<div style="float:left;"></div>
		<span style="float:right; margin-top:7px;">
			<input type="submit" class="emailButton picsbutton positive" value="Find Username" name="button" />
			<input type="submit" class="usernameButton picsbutton positive" value="Reset Password" name="button" />
		</span>
		</li>
	</ol>
	</fieldset>
</s:form>
<div class="info">
	We made improvements to our site and contractors can now have multiple user accounts. 
	If you are the secondary or billing contact for your account please type in your email address to retrieve 
	your new username. If you have problems, please <a href="Contact.action">contact us directly</a>.
</div>
</body>
</html>