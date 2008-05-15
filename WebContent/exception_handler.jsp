<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page isErrorPage="true" language="java"
	import="java.util.*, java.io.*"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="mailer" class="com.picsauditing.mail.EmailSender" scope="page" />


<%
/*
	If the exception is coming from the non-struts world, the URI will be the actual JSP page
	requested.  In the struts world, the uri will actually be this page, as a second action invocation.
	
	In normal JSP, the exception variable is an implicit variable on an error page, but coming from struts
	we have to pull it off the value stack and assign it ourselves to fit struts exceptions into the same 
	error page.
*/
	if( request.getRequestURI().endsWith("exception_handler.jsp" ) )  		
	{																  		
%>
		<s:set name="exception" value="%{exception}" scope="page"/>			
<%																			
		exception = (Exception) pageContext.getAttribute( "exception" );	
	}
%>




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
			email.append("An error occurred on PICS\n\n");
			email.append(message);
			email.append("\n\nServerName: " + request.getServerName());
			email.append("\nRequestURI: " + request.getRequestURI());
			email.append("\nQueryString: " + request.getQueryString());
			email.append("\nRemoteAddr: " + request.getRemoteAddr());
			if (permissions.isLoggedIn()) {
				email.append("\nName: " + permissions.getName());
				email.append("\nUsername: " + permissions.getUsername());
				email.append("\nAccountID: " + permissions.getAccountId());
				if (permissions.getAdminID() > 0) email.append("\nAdmin: " + permissions.getAdminID());
				email.append("\nType: " + permissions.getAccountType());
			} else {
				email.append("\nThe current user was NOT logged in.");
			}
	
			if (stacktrace.length() > 0) {
				email.append("\n\nTrace:\n");
				email.append(stacktrace);
			}
			email.append("\n\n");
			for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			    String headerName = (String)e.nextElement();
				email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
			}
			
			mailer.sendMail("PICS Exception Error", email.toString(), "errors@picsauditing.com", "errors@picsauditing.com");
			
		} catch (Exception e) {
			// do nothing
		}
	}
%>
<html>
<head>
<title>PICS Error</title>
</head>
<body>
<h1>An unexpected error occurred</h1>

<% if (debugging) { %>
	<p><%=stacktrace %></p>
<% } else { %>
	<p><a href="#" onClick="history.back()">Click to return to the
		previous page</a><br /><br />
		We apologize for this inconvenience and have notified our engineers of
		your problem. <br>
		<br>
		If you continue to receive this error, please call Customer Service at
		949.387.1940 extension 1</p>
		<p class="redMain">Error: <%=message%></p>
<% } %>
</body>
</html>
