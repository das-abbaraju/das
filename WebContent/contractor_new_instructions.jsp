<%@ page language="java" errorPage="exception_handler.jsp"%>
<%
	boolean submitted = (null != request.getParameter("submit.x"));
	if (submitted) {
		response.sendRedirect("contractor_new.jsp");
		return;
	}
%>
<html>
<head>
<title>Registration Instructions</title>
<meta name="color" content="#669966" />
<meta name="flashName" content="REGISTER" />
<meta name="iconName" content="register" />
</head>
<body>
<form name="form1" method="post"
	action="contractor_new_instructions.jsp">
<table width="520" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top" class="blueMain">
		<p align="justify" class="redMain">If you're an operator and are
		interested in using our services, please <a href="contact.jsp"
			class="redMain">contact us</a> directly.</p>
		<table border="0" cellpadding="0" cellspacing="0" class="blueMain">
			<tr>
				<td width="245" valign="top" class="blueMainServices">To create
				a contractor account, you will choose a username and password with
				which you will log into PICS in the future. You will also be
				required to enter contact information for your company.</td>
				<td width="25">&nbsp;</td>
				<td>By creating a profile, you agree to be bound by our <a
					href="forms/form2.pdf" title="Terms of use" target="_blank">contractor's
				agreement</a>. <br>
				If you agree and would like to create an account, click the continue
				button below.</td>
			</tr>
		</table>
		<br>
		<p align="center"><input type="image" name="submit" id="submit"
			value="Continue" src="images/button_continue.jpg" width="84"
			height="27" border="0"></p>
		</td>
	</tr>
</table>
</form>
<br>
<br>
</body>
</html>
