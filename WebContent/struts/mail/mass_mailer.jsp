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

<div id="search">
<div id="filters">
	<div class="filterOption">Tokens:<br />
		<s:select id="tokens" name="tokens" size="10" listKey="id" listValue="subject" list="emailTemplates"></s:select>
	</div>
	<div class="filterOption">Contractors:<br />
		<s:select id="contractors" name="contractors" size="10" list="emailTemplates"></s:select>
	</div>
	<div class="filterOption">Email Templates:<br />
		<s:select id="emailTemplates" name="emailTemplates" listKey="id" listValue="subject" list="emailTemplates"></s:select>
	</div>
	<div class="filterOption">Email Draft:<br />
		<s:textarea name="emailPreview" cols="75" rows="20"></s:textarea>
	</div>
	<div class="filterOption">Email Preview:<br />
		<s:textarea name="emailPreview" cols="75" rows="20"></s:textarea>
	</div>	
	
	<br clear="all" />
	<div class="buttons">
		<button class="positive" name="button" type="submit" value="Search">Send Email</button>
	</div>	
</div>
</div>

</s:form>

</body>
</html>