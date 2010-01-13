<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login</title>
<meta name="accountrecovery" content="Account_Recovery">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<style>
fieldset.form input.login {
	padding: 3px;
	font-size: 16px;
	width: 240px;
	font-weight: bold;
	font-family: 'Trebuchet MS', Helvetica, sans-serif;
}
</style>
</head>
<body>

<s:include value="../actionMessages.jsp"></s:include>
<div class="left" style="margin-right:40px;">
<s:form id="login" cssStyle="width: 500px;">
	<fieldset class="form" style="margin-top: 20px;"><legend><span>Reset Password</span></legend>
	<ol>
		<li>Enter the user name which you created your PICS company profile with. After verifying your identity through the email associated
		with your PICS account, we will reset your password and send you an additional email with the new password. If you have any problems, <a href="Contact.action" title="Contact PICS">contact us</a>
		directly.</li>
		<li><label style="margin-top: 7px;">Username:</label> <s:textfield name="username" cssClass="login" /></li>
		<li>
		<div class="buttons" style="padding-left: 200px; padding-bottom: 50px;">
		<input type="submit" class="picsbutton positive" value="Reset Password" name="button" />
		</div>
		</li>
		<li><a href="Login.action">Return to Login Page</a></li>
	</ol>
	</fieldset>
	<fieldset class="form submit"></fieldset>
</s:form>
</div>
<div class="left">
<s:form id="login" cssStyle="width: 500px;">
	<fieldset class="form" style="margin-top: 20px;"><legend><span>Find Username</span></legend>
	<ol>
		<li>Enter the email address that you submitted when you created your PICS company profile and we will email you a list of all usernames associated with that email address at PICS. If you have any problems, <a href="Contact.action" title="Contact PICS">contact us</a>
		directly.</li>
		<li><label style="margin-top: 7px;">Email:</label> <s:textfield name="email" cssClass="login" /></li>
		<li>
		<div class="buttons" style="padding-left: 200px; padding-bottom: 50px;">
		<input type="submit" class="picsbutton positive" value="Find Username" name="button" />
		</div>
		</li>
		<li><a href="Login.action">Return to Login Page</a></li>
	</ol>
	</fieldset>
	<fieldset class="form submit"></fieldset>
</s:form>
</div>
</body>
</html>