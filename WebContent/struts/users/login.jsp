<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login</title>
</head>
<body onload="document.login.username.focus();">
<h1>Login to PICS</h1>

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="login">
<s:hidden name="button" value="login" />
<table border="0" cellpadding="2" cellspacing="0">
	<tr>
		<td width="138" align="right"><img src="images/login_user.gif"
			alt="User Name" width="50" height="9">&nbsp;</td>
		<td valign="top" class="blueMain"><s:textfield name="username"></s:textfield></td>
	</tr>
	<tr>
		<td width="138" align="right"><img src="images/login_pass.gif"
			alt="Password" width="50" height="9">&nbsp;</td>
		<td valign="top" class="blueMain"><s:password name="password"></s:password>
		&nbsp;&nbsp;Forget your password? <a href="forgot_password.jsp"
			class="redMain">click here</a></td>
	</tr>
	<tr>
		<td align="right" class="blueMain">&nbsp;</td>
		<td valign="top" class="redMain">
		<p><input name="Submit" type="image" id="Submit"
			src="images/button_login.jpg" width="65" height="28" border="0">
		<br>
		<br>
		If you're an operator and have interest in our services, please <a
			href="contact.jsp">contact us</a> directly.<br>
		If you're a contractor, you can <a
			href="contractor_new_instructions.jsp">create your own account
		online</a>.</p>
		</td>
	</tr>
</table>
</s:form>
</body>
</html>