<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.picsauditing.mail.Email"%>
<%@page import="com.picsauditing.mail.EmailTemplates"%>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<%@page import="com.picsauditing.mail.EmailUserBean"%>
<%@page import="com.picsauditing.mail.EmailBean"%>
<%@page import="com.picsauditing.mail.EmailAuditBean"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%
permissions.tryPermission(OpPerms.EmailTemplates);

EmailTemplates template = null;
try {
	template = EmailTemplates.valueOf(request.getParameter("template"));
} catch (Exception e) {
	
	template = EmailTemplates.welcome;  //a default
}

AppPropertyDAO apDAO = (AppPropertyDAO)SpringUtils.getBean("AppPropertyDAO");
if ("save".equals(request.getParameter("action"))) {
	AppProperty appProperty = new AppProperty();
	appProperty.setProperty("email_"+template+"_body");
	appProperty.setValue(request.getParameter("body"));
	apDAO.save(appProperty);
	appProperty.setProperty("email_"+template+"_subject");
	appProperty.setValue(request.getParameter("subject"));
	apDAO.save(appProperty);
}

Map<String, Object> tokens = new HashMap<String, Object>();
Email emailSample = new Email();
String accountID = request.getParameter("accountID");
if( accountID == null || accountID.length() == 0 )
{
	accountID = "3"; // Ancon Marine
}

String userID = request.getParameter("userID");
if( userID == null || userID.length() == 0 )
{
	userID = "941";  // tallred
}



String emailBody = "";
String subjectTemplate = "";
String bodyTemplate = "";

if (template != null) {
	EmailBean mailer = null;
	
	ContractorAuditDAO conAuditDAO = (ContractorAuditDAO)SpringUtils.getBean("ContractorAuditDAO");
	ContractorAudit conAudit = conAuditDAO.find(8301);
	AuditDataDAO aDataDAO = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
	AuditData auData = aDataDAO.findAnswerToQuestion(2566,1331);
		
	OperatorAccountDAO oppAcctDAO = (OperatorAccountDAO)SpringUtils.getBean("OperatorAccountDAO");
	OperatorAccount opAcct = oppAcctDAO.find(1813);
	
	CertificateDAO certificateDAO = (CertificateDAO)SpringUtils.getBean("CertificateDAO");
	Certificate certificate = certificateDAO.find(2767);
	
	if( template.getClassName().equals("EmailContractorBean")
		&& accountID!=null && accountID.length() > 0) {
		EmailContractorBean o = (EmailContractorBean) SpringUtils.getBean(template.getClassName());
		o.setTestMode(true);
		o.addToken("conAudit", conAudit);
		o.addToken("opAcct", opAcct);
		o.addToken("opName", opAcct.getName());
		if(certificate != null) {
			o.addToken("certificate_type",certificate.getType());
			o.addToken("expiration_date",certificate.getExpiration());
		}
		if(auData != null)
			o.addToken("safetyManual",auData);
		o.setPermissions(permissions);
		o.sendMessage(template, Integer.parseInt(accountID));
		mailer = o;
	} else if (template.getClassName().equals("EmailUserBean") 
		&& userID!=null && userID.length() > 0) {
		EmailUserBean o = (EmailUserBean) SpringUtils.getBean(template.getClassName());
		o.setTestMode(true);
		o.addToken("conAudit", conAudit);
		o.addToken("opAcct", opAcct);
		o.setPermissions(permissions);
		o.sendMessage(template, Integer.parseInt(userID));		
		mailer = o;
	} else if (template.getClassName().equals("EmailAuditBean")
			&& accountID!=null && accountID.length() > 0) {
		EmailAuditBean o = (EmailAuditBean) SpringUtils.getBean(template.getClassName());
		o.setTestMode(true);
		o.addToken("conAudit", conAudit);
		o.addToken("opAcct", opAcct);
		o.addToken("opName", opAcct.getName());
		o.setPermissions(permissions);
		o.sendMessage(template, Integer.parseInt(accountID));
		mailer = o;
	}
	
	tokens = mailer.getTokens();
	emailSample = mailer.getEmail(); //mailer.testMessage(template, accountID, permissions);
	emailBody = emailSample.getBody();
	subjectTemplate = apDAO.find("email_"+template+"_subject").getValue();
	bodyTemplate = apDAO.find("email_"+template+"_body").getValue();
}

%>

<%@page import="com.picsauditing.dao.ContractorAuditDAO"%>
<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%@page import="com.picsauditing.dao.AppPropertyDAO"%>
<%@page import="com.picsauditing.jpa.entities.AppProperty"%>
<%@page import="com.picsauditing.dao.OperatorAccountDAO"%>
<%@page import="com.picsauditing.jpa.entities.OperatorAccount"%>
<%@page import="com.picsauditing.dao.CertificateDAO"%>
<%@page import="com.picsauditing.jpa.entities.Certificate"%>
<%@page import="com.picsauditing.dao.AuditDataDAO"%>
<%@page import="com.picsauditing.jpa.entities.AuditData"%>
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
<div style="font-size: 12px;">
<%
String tStart = "${";
String tEnd = "}";
for(String key : tokens.keySet()) {
	%><%=tStart+key+tEnd%> = <%=tokens.get(key).getClass()%><br /><%
}
%>
</div>
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