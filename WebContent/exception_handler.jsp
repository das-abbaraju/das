<%@ page isErrorPage="true" language="java"
	import="java.util.*, java.io.*, com.opensymphony.xwork2.ActionContext"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.opensymphony.xwork2.ActionContext"%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<%@page import="com.picsauditing.mail.EmailSender"%>

<%@page import="com.picsauditing.mail.SendMail"%><html>
<%
/*
	If the exception is coming from the non-struts world, ActionContext.getContext().getActionInvocation() will 
	be null.
	
	In normal JSP, the exception variable is an implicit variable on an error page, but coming from struts
	we have to pull it off the value stack and assign it ourselves to fit struts exceptions into the same 
	error page.
*/
	

	if( ActionContext.getContext().getActionInvocation() != null )
	{
		pageContext.setAttribute( "exception", ActionContext.getContext().getValueStack().findValue("exception") );
		
		exception = (Exception) pageContext.getAttribute( "exception" );	
	}
%>




<%
	boolean debugging = "1".equals(System.getProperty("pics.debug"));
	
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
		EmailQueue mail = new EmailQueue();
		mail.setSubject("PICS Exception Error");
		mail.setBody(email.toString());
		mail.setToAddresses("errors@picsauditing.com");
		try {
			EmailSender.send(mail);
		} catch (Exception e) {
			SendMail sendMail = new SendMail();
			mail.setFromAddress("PICS Exception Handler <info@picsauditing.com>");
			sendMail.send(mail); 
		}
	}
%>
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
