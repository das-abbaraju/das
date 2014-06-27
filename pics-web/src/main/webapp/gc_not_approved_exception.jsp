<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page isErrorPage="true" language="java" import="com.opensymphony.xwork2.ActionContext" %>

<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

<%@ page import="com.opensymphony.xwork2.ActionContext" %>

<%
	if(ActionContext.getContext().getActionInvocation() != null) {
		pageContext.setAttribute("exception", ActionContext.getContext().getValueStack().findValue("exception"));
		exception = (Exception) pageContext.getAttribute("exception");
	}

	String message = "";

	if (exception != null) {
		if (exception.getMessage() != null) {
			message = exception.getMessage();
		} else {
			message = exception.toString();
		}
	}
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