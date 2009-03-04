<%@ page isErrorPage="true" language="java"
	import="java.util.*, java.io.*, com.opensymphony.xwork2.ActionContext"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="mailer" class="com.picsauditing.mail.EmailSender" scope="page" />


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
	}

%>
<%@page import="com.opensymphony.xwork2.ActionContext"%>
<html>
<head>
<title>PICS Error</title>
</head>
<body>
<h1>Contractor not found</h1>

<div id="alert">
	We could not locate the contractor you were looking for.  Please try again later.
</div>


	<p><a href="#" onClick="history.back()">Click to return to the
		previous page</a><br /><br />
		We apologize for this inconvenience and have notified our engineers of
		your problem. <br />
		<br />
		If you continue to receive this error, please call Customer Service at
		949.387.1940 extension 1</p>
		<p class="redMain">Error: <%=message%></p>

</body>
</html>