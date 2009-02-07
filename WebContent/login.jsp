<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<%
	String lname = "";
	String lpass = "";
	String msg = "";

	String switchUser = request.getParameter("switchUser");
	if (switchUser != null) {
		LoginController loginCtrl = new LoginController();
		if (loginCtrl.loginByAdmin(switchUser, request, response))
			return;
		else
			msg = loginCtrl.getErrorMessages();
	}

	if (request.getParameter("username") != null) {
		LoginController loginCtrl = new LoginController();
		if (loginCtrl.login(request.getParameter("username"), request
				.getParameter("password"), request, response))
			return;
		else
			msg = loginCtrl.getErrorMessages();
	}

	String username_email = request.getParameter("uname");
	if (!"".equals(username_email) && null != username_email) {
		aBean.updateEmailConfirmedDate(username_email);
		msg = "Thank you for confirming your email address. Please login to access the site.";
	}
	String temp = request.getParameter("msg");
	if (null != temp && temp.length() > 0)
		msg = temp;
%>
<html>
<head>
<title>Login</title>
</head>
<body onload="document.login.username.focus();">
<form action="login.jsp" method="post" name="login" id="login">
<table border="0" cellpadding="2" cellspacing="0">
	<tr>
		<td width="138" class="blueMain">&nbsp;</td>
		<td class="redMain"><strong><%=aBean.getErrorMessages()%><%=msg%></strong></td>
	</tr>
	<tr>
		<td width="138" align="right"><img src="images/login_user.gif"
			alt="User Name" width="50" height="9">&nbsp;</td>
		<td valign="top" class="blueMain">
		<p><input name="username" type="text" class="forms" id="username"
			value="<%=lname%>">
		</td>
	</tr>
	<tr>
		<td width="138" align="right"><img src="images/login_pass.gif"
			alt="Password" width="50" height="9">&nbsp;</td>
		<td valign="top" class="blueMain"><input name="password"
			type="password" class="forms" id="password" value="<%=lpass%>">
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
</form>
</body>
</html>