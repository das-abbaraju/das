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

<head>
    <title>PICS Error</title>
    
    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
    
    <jsp:include page="struts/jquery.jsp"/>
    
    <script type="text/javascript">
        $(document).ready(function() { 
            $('#response_form').submit(function() {
            	var priority = $('input[name="priority"]:checked').val();
            	var user_message = $("textarea#user_message").val();
            	var to_address = "errors@picsauditing.com";
            	var from_address = $("#from_address").val();
            	var user_name = $("#user_name").val();
            	var dataString = 'priority=' + priority + '&user_message=' + user_message + '&to_address=' + to_address + '&from_address=' + from_address + '&exceptionID=' + <%= exceptionID %> + '&user_name=' + user_name;
            	
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
    <h1>
    	<s:text name="Exception.NoRights.Title" />
    </h1>
    <div class="alert"><%=message%></div>
    
    <% if (debugging) { %>
    	<p><%= stacktrace %></p>
    <% } else { %>
    	<div style="padding-bottom:15px;" >
    		<input class="picsbutton" type="button" value="&lt;&lt;" onclick="window.history.back().back()" />
    		<input class="picsbutton" type="button" value="<s:text name="Exception.ReportToPicsEngineers" />" onclick="$('#user_message').toggle(); return false;" />
    	</div>
        
    	<div id="user_message" style="display:none;">
    		<form id="response_form" method="post" action="" style="width:650px;">
    			<fieldset class="form">
    				<div style="padding:2ex;">
    				    <s:text name="Exception.Form.Text" />
                        <label style="padding-top: 2ex;"><s:text name="global.Priority" />:</label>
    					<span>
    						<s:text name="LowMedHigh.Low" />&nbsp;&nbsp;&nbsp;
    						<input type="radio" name="priority" value="1" checked />1
    						<input type="radio" name="priority" value="2" />2
    						<input type="radio" name="priority" value="3" />3
    						<input type="radio" name="priority" value="4" />4
    						<input type="radio" name="priority" value="5" />5
    						&nbsp;&nbsp;&nbsp;<s:text name="LowMedHigh.High" /><br/>
    					</span>
                        
    					<s:text name="Exception.Form.Message" />:
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
        									<input class="picsbutton" style="float:right;" type="submit" value="<s:text name="AuditStatus.Submitted.button" />" onclick="$('#backButton').fadeIn(1500)"/>
        								</span>
        							</td>
        						</tr>		
        					</table>
    					</div>
    				</div>
    			</fieldset>
    		</form>
    	</div>
    <% } %>
</body>