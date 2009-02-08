<%@ page language="java" %>
<html>
<head>
<title>Error</title>
</head>
<body>

<table align="center" style="margin: 100px;">
	<tr><td class="redMain">
		<p><b>Sorry!</b> We've updated our website and this page no longer exists.
		Please update your bookmarks and link to our <b>new login page</b>.</p>
		</td>
	</tr>
	<tr>
		<td class="blueMain"><b>http://www.picsauditing.com/Login.action</b></td>
	</tr>
	
<%
	String params = "";
	if (request.getParameter("username") != null) {
		// Trying to login
		params = "?button=login&username=" + request.getParameter("username") +
				"&password=" + request.getParameter("password");
	}
	if (request.getParameter("uname") != null) {
		// Trying to login
		params = "?button=confirm&username=" + request.getParameter("uname");
	}
%>
	
	<tr>
		<td class="blueMain"><a href="Login.action<%= params %>">Click Here to Continue</a></td>
	</tr>
</table>

</body>
</html>