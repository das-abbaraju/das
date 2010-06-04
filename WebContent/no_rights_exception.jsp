<%@ page isErrorPage="true" language="java"
	import="java.util.*, java.io.*, com.opensymphony.xwork2.ActionContext"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

<%@page import="com.opensymphony.xwork2.ActionContext"%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%@page import="com.picsauditing.search.Database"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Timestamp"%>
<html>

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

	// writing initial exception
	Database db = new Database();
	long exceptionID = -1;
	try {
		if(permissions != null)
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,createdBy,creationDate) VALUES ('"+exception.getClass().getSimpleName()+"',"+1+","+permissions.getUserId()+",'"+new Timestamp(System.currentTimeMillis())+"')");
		else
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,creationDate) VALUES ('"+exception.getClass().getSimpleName()+"',"+1+",'"+new Timestamp(System.currentTimeMillis())+"')");
	} catch (SQLException e) {}
%>

<head>
<title>PICS Error</title>
<jsp:include page="struts/jquery.jsp"/>
<script type="text/javascript">
    $(document).ready(function() { 
        $('#response_form').submit(function() {
        	var priority = $('input[name=priority]:checked').val();
        	var user_message = $("textarea#user_message").val();
        	var to_address = "info@picsauditing.com";
        	var from_address = $("#from_address").val();
        	var user_name = $("#user_name").val();
        	var dataString = 'priority='+ priority + '&user_message=' + user_message + '&to_address=' + to_address + '&from_address=' + from_address + '&exceptionID=' + <%= exceptionID %> + '&user_name=' + user_name;  
        	$.ajax({  
        		type: "POST",  
        		url: "send_exception_email.jsp",  
        		data: dataString,  
        			success: function() {  
        				$('#response_form').html("<div id='message'></div>");  
        				$('#message').html("<h3>Response Submitted!</h3>")  
        				.append("<h5>We will be in touch soon.</h5>")  
        				.hide()  
        				.fadeIn(1500);  
        			}  
        		});  
        	return false;  
        }); 
    }); 
</script> 
</head>
<body>
<h1>You do not have permission to access this page.</h1>
<div class="alert"><%=message%></div>

<% if (debugging) { %>
	<p><%=stacktrace %></p>
<% } else { %>
	<input class="picsbutton" type="button" value="Report this to PICS" onclick="$('#user_message').toggle(); return false;" />
	<input class="picsbutton" type="button" value="Return to the previous page" onclick="window.history.back().back()" />
	<div id="user_message" style="display:none;">
		<p><br />
		We apologize for this inconvenience. <br/>If you continue to receive this message and believe it is an error, please report it to us using the form below or call Customer Service at
			949.387.1940 extension 1</p>
			
		<form id="response_form" method="post" action="" >
			<fieldset class="form">
				<span>Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="text" id="user_name" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
					<br/>
					Email:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="text" id="from_address" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
					<br/>
					Priority:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Low&nbsp;&nbsp;&nbsp;
					<input type="radio" name="priority" value="1" checked />1
					<input type="radio" name="priority" value="2" />2
					<input type="radio" name="priority" value="3" />3
					<input type="radio" name="priority" value="4" />4
					<input type="radio" name="priority" value="5" />5
					&nbsp;&nbsp;&nbsp;High<br/>
				</span>

				<div style="padding-top:10px;">Please describe the problem:
					<table>
						<tr>
							<td>
								<textarea id="user_message" name="user_message" rows="3" cols="40" style="color:#464646;font-size:12px;font-weight:bold;"></textarea>
							</td>
						</tr>
						<tr>
							<td>
								<input class="picsbutton" style="float:right;" type="submit" value="Submit" />
								<input class="picsbutton" style="float:right;" type="button" value="Back" onclick="window.history.back().back()"/>
							</td>
						</tr>		
					</table>
				</div>
			</fieldset>
		</form>
	</div>
<% } %>
</body>
</html>
