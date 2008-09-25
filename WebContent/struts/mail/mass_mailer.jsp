<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Mass Mailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<style>
td.selected {
	background-color: #FEC;
}
</style>
</head>
<body>
<h1>Mass Mailer</h1>

<s:include value="../actionMessages.jsp"></s:include>
<s:form id="form1" method="post">

<table>
	<tr>
		<td>
		</td>
		<td>
			Email Templates:<s:select id="emailTemplates" name="emailTemplates" listKey="id" listValue="subject" list="emailTemplates"></s:select>
		</td>
	</tr>
	<tr>
		<td>
			Tokens:<s:select id="tokens" name="tokens" size="10" listKey="id" listValue="subject" list="emailTemplates"></s:select>
		</td>
		<td>
			Email Draft:<s:textarea name="emailPreview" cols="75" rows="20"></s:textarea>
		</td>
	</tr>
	<tr>
		<td>
			Contractors:<s:select id="contractors" name="contractors" size="10" list="emailTemplates"></s:select>
		</td>
		<td>
			Email Preview:<s:textarea name="emailPreview" cols="75" rows="20"></s:textarea>
		</td>
	</tr>
	
</table>

	
	<div class="buttons">
		<button class="positive" name="button" type="submit" value="Search">Send Email</button>
	</div>	

</s:form>

</body>
</html>