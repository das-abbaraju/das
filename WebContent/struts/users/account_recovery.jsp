<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
<title>Account Recovery</title>
<meta name="accountrecovery" content="Account_Recovery">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
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
<s:form id="accountRecovery">
	<h1>Account Recovery</h1>
	<div style="margin-bottom: 10px;">
		<a href="Login.action">&lt;&lt; Return to Login Page</a>
	</div>
	<div style="width: 500px;">
		<fieldset class="form">
		<h2 class="formLegend">
			<span class="form-title">Account Recovery</span>
		</h2>
		<ol style="margin-top:7px;">
			<li>
				<div><label>Username:</label> <s:textfield id="usernameBox" name="username" cssClass="login" onchange="showUsernameData()" onclick="showUsernameData()"/></div>
				<div class="fieldhelp">
					<h3>Username</h3>
					<p>Please input your username you use to login here, if you do not remember it then use the Find Username option below</p>
				</div>
			</li>
			<li>
				<div><label>Email:</label><s:textfield id="emailBox" name="email" cssClass="login" size="28" onchange="showEmailData()" onclick="showEmailData()"/></div>
				<div class="fieldhelp">
					<h3>Email</h3>
					<p>Please input the email used with your account</p>
				</div>
			</li>
				
			<li>
				<div class="usernameMessage">Enter your username and we will send you an email containing a link that you can use to reset the password on your account. If you have any problems, please check your spam filters or <a href="Contact.action" title="Contact PICS">contact us</a> directly.</div>
				<div class="emailMessage">Enter your email address to receive the username(s) associated with that address. If you have any problems, please check your spam filters or <a href="Contact.action" title="Contact PICS">contact us</a> directly.</div>
			</li>
			<li>
				<label>Enter Verification:</label>
				<s:property value="recaptcha.recaptchaHtml" escape="false"/>
				<div class="fieldhelp">
					<h3>Verification</h3>
					<p>Please complete the recaptcha to the left, this helps us to keep your information safe</p>
				</div>
			</li>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" value="Find Username" name="button" />
			<input type="submit" class="picsbutton positive" value="Reset Password" name="button" />
		</fieldset>
	</div>
</s:form>
</body>
</html>