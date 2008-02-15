<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="java.util.HashMap"%>
<%@page import="com.picsauditing.mail.Email"%>
<%@page import="com.picsauditing.mail.EmailTemplates"%>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<%@page import="com.picsauditing.mail.EmailUserBean"%>
<%
permissions.tryPermission(OpPerms.EmailTemplates);

EmailTemplates template = null;
Email emailSample = new Email();
HashMap<String, String> tokens = new HashMap<String, String>();
try {
	template = EmailTemplates.valueOf(request.getParameter("template"));
} catch (Exception e) {}

String accountID = request.getParameter("accountID");
String userID = request.getParameter("userID");
String emailBody = "";

if (template != null) {
	if (accountID!=null && accountID.length() > 0) {
		EmailContractorBean mailer = new EmailContractorBean();
		emailSample = mailer.testMessage(template, accountID, permissions);
		tokens = mailer.getMerge().getTokens();
	} else if (userID!=null && userID.length() > 0) {
		EmailUserBean mailer = new EmailUserBean();
		//emailSample = mailer.testMessage(template, accountID, permissions);
	}
	emailBody = emailSample.getBody();
	//emailBody.replaceAll("r", "<br />");
}

pageBean.setTitle("Manage Email Templates");
%>
<%@ include file="includes/header.jsp" %>

<table border="0">
<tr valign="top">
<td>
<br /><br />
<form action="email_templates.jsp" method="get">
<select name="template">
	<option value=""> - Choose - </option>
	<%
	for(EmailTemplates i : EmailTemplates.values()) {
		%><option value="<%=i%>" <%=i.equals(template)?"SELECTED":"" %>  ><%=i.getDescription()%></option><%
	}
	%>
</select><br />
User:<input type="text" name="userID" size="6" value="<%=userID %>" /><br />
Account:<input type="text" name="accountID" size="6" value="<%=accountID %>" /><br />
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
</td>
</tr>
</table>

<%@ include file="includes/footer.jsp" %>
