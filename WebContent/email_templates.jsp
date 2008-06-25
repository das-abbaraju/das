<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.picsauditing.mail.Email"%>
<%@page import="com.picsauditing.mail.EmailTemplates"%>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<%@page import="com.picsauditing.mail.EmailUserBean"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<jsp:useBean id="props" class="com.picsauditing.PICS.AppPropertiesBean" scope ="page"/>
<%
permissions.tryPermission(OpPerms.EmailTemplates);

EmailTemplates template = null;
try {
	template = EmailTemplates.valueOf(request.getParameter("template"));
} catch (Exception e) {
	
	template = EmailTemplates.welcome;  //a default
}

if ("save".equals(request.getParameter("action"))) {
	props.save("email_"+template+"_body", request.getParameter("body"));
	props.save("email_"+template+"_subject", request.getParameter("subject"));
}

Map<String, Object> tokens = new HashMap<String, Object>();
Email emailSample = new Email();
String accountID = request.getParameter("accountID");
if( accountID == null || accountID.length() == 0 )
{
	accountID = "3";
}

String userID = request.getParameter("userID");
if( userID == null || userID.length() == 0 )
{
	userID = "3";
}



String emailBody = "";
String subjectTemplate = "";
String bodyTemplate = "";

if (template != null) {
	
	EmailContractorBean mailer = (EmailContractorBean) SpringUtils.getBean(template.getClassName());
	mailer.setTestMode(true);
	
	
	if( template.getClassName().equals("EmailContractorBean")
		&& accountID!=null && accountID.length() > 0) {
		mailer.sendMessage(template, Integer.parseInt(accountID));		
	} else if (template.getClassName().equals("EmailUserBean") 
		&& userID!=null && userID.length() > 0) {
		mailer.sendMessage(template, Integer.parseInt(userID));		
	}

	tokens = mailer.getTokens();
	emailSample = mailer.getEmail(); //mailer.testMessage(template, accountID, permissions);

	
	emailBody = emailSample.getBody();
	//emailBody.replaceAll("r", "<br />");
	subjectTemplate = props.get("email_"+template+"_subject");
	bodyTemplate = props.get("email_"+template+"_body");
}

%>

<html>
<head>
<title>Manage Email Templates</title>

<script type="text/javascript">
	function switchUI( selectBox ) {
		var theText = selectBox.options[selectBox.selectedIndex].className;
		
		if( theText.indexOf('EmailContractorBean') != -1)
		{
			document.getElementById('userInput').style.display = 'none';	
			document.getElementById('accountInput').style.display = 'block';	
		}
		else if( theText.indexOf('EmailUserBean') != -1)
		{
			document.getElementById('userInput').style.display = 'block';
			document.getElementById('accountInput').style.display = 'none';	
		}
	}
</script>




</head>
<body>
<table border="0">
<tr valign="top">
<td>
<br /><br />
<form action="email_templates.jsp" method="get">
<select name="template" onchange="javascript: switchUI(this)">
	<option value=""> - Choose - </option>
	<%
	for(EmailTemplates i : EmailTemplates.values()) {
		%><option class="<%=i.getClassName()%>" value="<%=i%>" <%=i.equals(template)?"SELECTED":"" %>  ><%=i.getDescription()%></option><%
	}
	%>
</select><br />
<div id="userInput" <%= template != null && template.getClassName().equals("EmailUserBean") ? " " : " style=\"display : none;\" " %>>User:<input type="text" name="userID" size="6" value="<%=userID %>" /><br /></div>
<div id="accountInput" <%= template != null && template.getClassName().equals("EmailContractorBean") ? " " : " style=\"display : none;\" " %>>Account:<input type="text" name="accountID" size="6" value="<%=accountID %>" /><br /></div>
<input type="submit" value="Preview Email" />
</form>
email_<%=template%>_subject<br>
email_<%=template%>_body<br><br>
<%
String tStart = "${";
String tEnd = "}";
for(String key : tokens.keySet()) {
	%><%=tStart+key+tEnd%> = <%=tokens.get(key)%><br /><%
}
%>
</td>
<td width="600">
<h2><%=(template==null)?"Pick an Email Template":template.getDescription() %></h2>
<b>To:</b> <%= emailSample.getToAddress() %><br />
<b>Cc:</b> <%= emailSample.getCcAddress() %><br />
<b>Subject:</b> <%= emailSample.getSubject() %><br />
<b>Body:</b><br />
<textarea rows="20" cols="75"><%= emailSample.getBody() %></textarea>
<br><br>
<form action="email_templates.jsp" method="post">
	<input type="hidden" name="action" value="save" />
	<input type="hidden" name="userID" value="<%=userID %>" />
	<input type="hidden" name="accountID" value="<%=accountID %>" />
	<input type="hidden" name="template" value="<%=template %>" />
	<input type="text" name="subject" size="50" value="<%=subjectTemplate %>" /><br>
	<textarea name="body" rows="20" cols="75"><%= bodyTemplate %></textarea>
	<input type="submit" value="Save Template" />
</form>
</td>
</tr>
</table>

</body>
</html>