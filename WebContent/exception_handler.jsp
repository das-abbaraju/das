<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page isErrorPage="true" language="java"
	import="com.picsauditing.PICS.*, java.util.*, java.io.*"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean"
	scope="session" />
<html>
<head>
<title>PICS Error</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
<%
	boolean debugging = application.getInitParameter("environmentType").equals("development");
	String message = "";
	String cause = "Undetermined";
	String stacktrace = "";

	if (exception != null) {
		if (exception.getMessage() != null)
			message = exception.getMessage();
		else
			message = exception.toString();
		if (exception.getCause() != null)
			cause = exception.getCause().getMessage();
		
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		stacktrace = sw.toString();
	}//if

	if (!debugging) {
		try {
			StringBuilder email = new StringBuilder();
			email.append("An error occurred on PICS\n");
			email.append("\nServerName: " + request.getServerName());
			email.append("\nRequestURI: " + request.getRequestURI());
			email.append("\nQueryString: " + request.getQueryString());
			for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			    String headerName = (String)e.nextElement();
				email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
			}
			email.append("\nPathInfo: " + request.getPathInfo());
			email.append("\nContextPath: " + request.getContextPath());
			email.append("\nRemoteAddr: " + request.getRemoteAddr());
			email.append("\nRemoteUser: " + request.getRemoteUser());
			if (pBean.loggedIn) {
				email.append("\nLogged in as:");
				email.append("\nuserID: " + pBean.userID);
				email.append("\nuserName: " + pBean.userName);
				email.append("\nuserType: " + pBean.userType);
			} else {
				email.append("\nThe current user was NOT logged in.");
			}
	
			email.append("\n\n");
			email.append(message);
			email.append("\n\nTrace:\n\n");
			email.append(stacktrace);
			EmailBean.sendErrorMessage(email.toString());
		} catch (Exception e) {
			// do nothing
		}
	}
%>
</head>

<body bgcolor="EEEEEE">
<p>&nbsp;</p>
<table width="500" border="0" align="center" cellpadding="15"
	cellspacing="0" bordercolor="#CCCCCC" bgcolor="#FFFFFF">
	<tr>
		<td><a href="index.jsp"><img src="images/logo.gif" alt="HOME"
			width="146" height="145" border="0"></a></td>
		<td class="blueMain">
		<h2>An unexpected error occurred</h2>
	<% if (debugging) { %><%=stacktrace %><% } else { %>
		<a href="#" onClick="history.back()">Click to return to the
		previous page</a><br />
		We apologize for this inconvenience and have notified our engineers of
		your problem. <br>
		<br>
		If you continue to receive this error, please call Customer Service at
		949.387.1940 ext 1
		<table border="1">
			<tr>
				<td class="redMain">Error: <%=message%><br />
				Caused by: <%=cause%></td>
			</tr>
		</table>
	<% } %>
		</td>
	</tr>
</table>
<br>
<br>
<br>
</body>
</html>
