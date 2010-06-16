<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page isErrorPage="true" language="java"
	import="java.util.*,java.io.*,com.opensymphony.xwork2.ActionContext"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.opensymphony.xwork2.ActionContext"%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%@page import="com.picsauditing.mail.SendMail"%>
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

	if (ActionContext.getContext().getActionInvocation() != null) {
		pageContext
				.setAttribute("exception", ActionContext.getContext().getValueStack().findValue("exception"));

		exception = (Exception) pageContext.getAttribute("exception");
	}

	boolean debugging = "1".equals(System.getProperty("pics.debug"));

	String message = "";
	String cause = "Undetermined";
	String stacktrace = "";

	Date currentTime = new Date();
	// if the session hasn't been alive for a second, then redirect to the home page
	// when encountering an exception, otherwise write an email out
	if ((currentTime.getTime() - session.getCreationTime()) < 1000) {
		String redirectURL = "http://www.picsorganizer.com/";
		response.sendRedirect(redirectURL);
	} else {
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
			if (permissions.getAdminID() > 0)
				email.append("\nAdmin: " + permissions.getAdminID());
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
			String headerName = (String) e.nextElement();
			email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
		}
		EmailQueue mail = new EmailQueue();
		mail.setSubject("PICS Exception Error");
		mail.setBody(email.toString());
		mail.setToAddresses("errors@picsauditing.com");
		try {
			EmailSender.send(mail);
		} catch (Exception e) {
			System.out.println("PICS Exception Handler ... sending email via sendMail");
			SendMail sendMail = new SendMail();
			mail.setFromAddress("\"PICS Exception Handler\"<info@picsauditing.com>");
			sendMail.send(mail);
			System.out.println(mail.getBody());
		}
	}

	// writing initial exception
	Database db = new Database();
	long exceptionID = -1;
	String canonName = exception.getClass().getCanonicalName();
	String simpleName = exception.getClass().getSimpleName();
	String name = exception.getClass().getName();
	int userID = permissions.getUserId();
	try {
		if(permissions != null)
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,createdBy,creationDate,message) VALUES ('"+exception.getClass().getSimpleName()+"',"+1+","+permissions.getUserId()+",'"+new Timestamp(System.currentTimeMillis())+"','"+stacktrace+"')");
		else
			exceptionID = db.executeInsert("INSERT INTO app_error_log (category,priority,creationDate,message) VALUES ('"+exception.getClass().getSimpleName()+"',"+1+",'"+new Timestamp(System.currentTimeMillis())+"','"+stacktrace+"')");
	} catch (SQLException e) {}
%>

<head>
<jsp:include page="struts/jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<script type="text/javascript">
    $(document).ready(function() { 
        $('#response_form').submit(function() {
        	var user_message = $("textarea#user_message").val();
        	var to_address = "errors@picsauditing.com";
        	var from_address = $("#from_address").val();
        	var user_name = $("#user_name").val();
        	var dataString = 'priority=5&user_message=' + user_message + '&to_address=' + to_address + '&from_address=' + from_address + '&exceptionID=' + <%= exceptionID %> + '&user_name=' + user_name;  
        	$.ajax({  
        		type: "POST",  
        		url: "send_exception_email.jsp",  
        		data: dataString,  
        			success: function() {  
        				$('#response_form').html("<div id='message1'></div>");
        				$('#message1').html("<h3>Response Submitted!</h3>")
        				.append("<h5>Thank you for your assistance.</h5>")
        				.hide()  
        				.fadeIn(1500);  
        			}  
        		});  
        	return false;  
        }); 
    }); 
</script>
<title>PICS Error</title>
</head>
<body>
<div class="error">
	Oops!! An unexpected error just occurred.<br>
</div>
<% if (debugging) { %>
	<p><%=stacktrace %></p>
<% } else { %>
	<form id="response_form" method="post" action="" style="width:450px;">
		<fieldset class="form" >
			<legend><span>Please help us by reporting this error</span></legend>
			<div>
				<s:if test="!permissions.loggedIn">
					<div style="padding-top:40px;">
						<label style="width:5em;"><span>Name:</span></label>
						<input type="text" id="user_name" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
						<label style="width:5em;"><span>Email:</span></label>
						<input type="text" id="from_address" size="25" style="color:#464646;font-size:12px;font-weight:bold;"/>
						<br/>
					</div>
				</s:if>
	
				<div <s:if test="permissions.loggedIn">style="padding-top:35px;"</s:if>><label style="width:5em;">Optional:</label>Please tell us what you were trying to do:<br/>
					<label style="width:5em;">&nbsp;</label>
					<div>
					<table>
						<tr>
							<td>
								<textarea id="user_message" name="user_message" rows="3" cols="40" style="color:#464646;font-size:12px;font-weight:bold;"></textarea>
							</td>
						</tr>
						<tr>
							<td>
								<span>
									<input class="picsbutton" style="float:right;" type="submit" value="Report to PICS Engineers" onclick="$('#backButton').fadeIn(1500)"/>
									<input class="picsbutton" style="float:right;" type="button" value="&lt;&lt; Back" onclick="window.history.back().back()" />
								</span>
							</td>
						</tr>		
					</table>
					</div>
				</div>
			</div>
		</fieldset>
	</form>
	<input id="backButton" class="picsbutton" style="float:left; display:none;" type="button" value="&lt;&lt; Back" onclick="window.history.back().back()" />
<% } %>
</body>
</html>
