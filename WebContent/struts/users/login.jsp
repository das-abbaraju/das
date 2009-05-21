<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login</title>
<meta name="help" content="Logging_In">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
</head>
<body onload="document.forms['login'].username.focus();">

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="login" cssStyle="width: 500px;">
	<fieldset class="form" style="margin-top: 20px;">
	<legend><span>Login to PICS</span></legend>
	<ol>
		<li><label>Username:</label>
			<s:textfield name="username" /></li>
		<li><label>Password:</label>
			<s:password name="password" /></li>
		<li>Forget your password? <a href="forgot_password.jsp">Click here to have it sent to you</a></li>
		<li>Are you a contractor? <a
			href="contractor_new_instructions.jsp">Click to Register your company</a></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<div class="buttons">
			<button class="positive" value="login" name="button" type="submit">Login</button>
		</div>
	</fieldset>
</s:form>

</body>
</html>