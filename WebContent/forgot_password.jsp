<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>PICS - Forgot Password</title>
<META Http-Equiv="Cache-Control" Content="no-cache">
<META Http-Equiv="Pragma" Content="no-cache">
<META Http-Equiv="Expires" Content="0">
</head>
<body onLoad="document.form1.email.focus();">
<table width="657" cellpadding="0" cellspacing="0">
	<tr>
		<td width="145" class="blueMain"><br>
		</td>
		<td align="center" valign="top" class="blueMain">
		<form name="form1" method="post" action="Login.action">
			<input type="hidden" name="button" value="forgot" />
		<table width="0" border="0" cellspacing="0" cellpadding="1">
			<tr class="blueMain">
				<td colspan="2" class="blueHeader">Forget your password?</td>
			</tr>
			<tr>
				<td colspan="2" class="blueMain">Enter the email address that
				you submitted when you created your PICS company profile and we will
				email you your username and password. If you have any problems, <a
					href="Contact.action" title="Contact PICS">contact us</a> directly.</td>
			</tr>
			<tr>
				<td class="redMain" align="right">Email address&nbsp;</td>
				<td><input name="email" type="text" class="forms" size="25">
				<input name="submit" type="submit" class="forms"
					value="Send Password"></td>
			</tr>
		</table>
		</form>
		<br>
		<a href="Login.action">Return to Login Page</a></td>
		<td width="126" class="blueMain"></td>
	</tr>
</table>
</body>
</html>