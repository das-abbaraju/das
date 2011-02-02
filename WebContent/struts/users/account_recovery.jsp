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

$(function() {
	$('a.showUser').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
	$('a.showEmail').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
	
});

</script>
<style type="text/css">
.showEmail{
	display: none;
}
</style>
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
			<li class="showUser">
				<div><label>Username:</label> <s:textfield id="usernameBox" name="username" cssClass="login"/></div>
				<div class="fieldhelp">
					<h3>Username</h3>
					<p>Enter your username and we will send you an email containing a link that you can use to reset the password on your account. 
						If you have any problems, please check your spam filters or <a href="Contact.action" title="Contact PICS">contact us</a> directly</p>
				</div>
			</li>
			<li class="showEmail">
				<div><label>Email:</label><s:textfield id="emailBox" name="email" cssClass="login" size="28"/></div>
				<div class="fieldhelp">
					<h3>Email</h3>
					<p>Enter your email address to receive the username(s) associated with that address. If you have any problems, please check your spam filters or
					 <a href="Contact.action" title="Contact PICS">contact us</a> directly</p>
				</div>
			</li>
			<li>
				<a class="showUser showPointer">Forgot your username?</a>
				<a class="showEmail showPointer">Forgot your password?</a>
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
			<input type="submit" class="picsbutton positive showEmail" value="Find Username" name="button" />
			<input type="submit" class="picsbutton positive showUser" value="Reset Password" name="button" />
		</fieldset>
	</div>
</s:form>
</body>
</html>