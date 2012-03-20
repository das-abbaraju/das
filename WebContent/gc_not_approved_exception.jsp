<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page isErrorPage="true" language="java" import="java.util.*, java.io.*, com.opensymphony.xwork2.ActionContext" %>

<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

<%@ page import="com.opensymphony.xwork2.ActionContext" %>
<%@ page import="com.picsauditing.jpa.entities.EmailQueue" %>
<%@ page import="com.picsauditing.mail.EmailSender" %>
<%@ page import="com.picsauditing.search.Database" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Timestamp" %>

<%
/*
	If the exception is coming from the non-struts world, ActionContext.getContext().getActionInvocation() will 
	be null.
	
	In normal JSP, the exception variable is an implicit variable on an error page, but coming from struts
	we have to pull it off the value stack and assign it ourselves to fit struts exceptions into the same 
	error page.
*/

	if(ActionContext.getContext().getActionInvocation() != null) {
		pageContext.setAttribute("exception", ActionContext.getContext().getValueStack().findValue("exception"));
		
		exception = (Exception) pageContext.getAttribute("exception");
	}

	boolean debugging = "1".equals(System.getProperty("pics.debug"));
	
	String message = "";
	String cause = "Undetermined";
	String stacktrace = "";

	if (exception != null) {
		if (exception.getMessage() != null) {
			message = exception.getMessage();
		} else {
			message = exception.toString();
		}
        
		if (exception.getCause() != null) {
			cause = exception.getCause().getMessage();
		}
		
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		stacktrace = sw.toString();
	}//if

	// writing initial exception
	Database db = new Database();
	long exceptionID = -1;
    
	try {
		if (permissions != null) {
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,createdBy,creationDate) VALUES ('" + exception.getClass().getSimpleName() + "'," + 1 + "," + permissions.getUserId() + ",'" + new Timestamp(System.currentTimeMillis()) + "')");
		} else {
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,creationDate) VALUES ('" + exception.getClass().getSimpleName() + "'," + 1 + ",'" + new Timestamp(System.currentTimeMillis()) + "')");
		}
        
	} catch (SQLException e) {}
%>

<html>
<head>
<title>PICS Error</title>
</head>
<body>
<h1>General Contractor Not Approved</h1>

<div class="error">
	<%=message%>
	<br />
	<a href="#" onClick="history.back()">Click to return to the previous page</a>
</div>

</body>
</html>