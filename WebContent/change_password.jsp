<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%
	User userAction = new User();
	userAction.setFromDB(permissions.getUserIdString());
	UserDO user = userAction.userDO;
	if (request.getParameter("newPassword") != null) {
		user.password = request.getParameter("newPassword");
		userAction.writeToDB();
	}
%>
<html>
<head>
<title>Change Password</title>
</head>
<body>
<table width="657" cellpadding="10" cellspacing="0">
	<tr>
		<td width="125" bgcolor="#DDDDDD" class="blueMain"><br>
		</td>
		<td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
		<form name="form1" method="post">
		<table width="0" border="0" cellspacing="0" cellpadding="1">
			<tr align="center" class="blueMain">
				<td colspan="2" class="blueHeader">Change Password</td>
			</tr>
			<tr class="blueMain">
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td class="blueMain" align="right">Username:</td>
				<td class="blueMain"><b><%=user.username%></b></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">New Password</td>
				<td><input name="newPassword" type="text" class="forms"
					size="15"></td>
			</tr>
			<tr>
				<td class="blueMain" align="right">&nbsp;</td>
				<td><input name="submit" type="submit" class="forms"
					value="Change Password"></td>
			</tr>
		</table>
		</form>
		<br>
		</td>
		<td width="126" bgcolor="#DDDDDD" class="blueMain"></td>
	</tr>
</table>
</body>
</html>
