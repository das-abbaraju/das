<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login</title>
<meta name="help" content="Logging_In">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<style>
fieldset.form input.login {
	padding: 5px;
	font-size: 16px;
	width: 240px;
	font-weight: bold;
	font-family: courier;
}
</style>
</head>
<body onload="document.forms['login'].username.focus();">

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="login" cssStyle="width: 500px;">
	<fieldset class="form" style="margin-top: 20px;"><legend><span>Login to PICS</span></legend>
	<ol>
		<li><label style="margin-top: 7px;">Username:</label> <s:textfield name="username" cssClass="login" /></li>
		<li><label style="margin-top: 7px;">Password:</label> <s:password name="password" cssClass="login" /></li>
		<li>
		<div class="buttons" style="padding-left: 200px; padding-bottom: 50px;">
		<button class="positive" value="login" name="button" type="submit">Login</button>
		</div>
		</li>
		<li>Forget your password? <a href="forgot_password.jsp">Click here to have it sent to you</a></li>
		<li>Are you a contractor? <a href="contractor_new_instructions.jsp">Click to Register your company</a></li>
	</ol>
	</fieldset>
	<fieldset class="form submit"></fieldset>
</s:form>

</body>
</html>