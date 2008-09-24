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
	<div class="filterOption">Token Toolbox:<br />
		<s:select id="tokens" name="tokens" multiple="true" size="10" listKey="questionID" listValue="question" list="typeOfWork"></s:select>
	</div>
	<div class="filterOption">Email Draft:<br />
		<s:textarea label="draft" name="draft" cols="20" rows="75"></s:textarea>
	</div>
	<div class="filterOption">Recipients:<br />
		<s:select id="recipients" name="recipients" multiple="true" size="10" listKey="conId" listValue="name" list="recipients"></s:select>
	</div>
	<div class="filterOption">Email Preview:<br />
		<s:textarea label="preview" name="preview" cols="20" rows="75"></s:textarea>
	</div>
	<div class="buttons">
		<button class="positive" name="button" type="submit" value="send">Send Email</button>
	</div>		
</div>
</s:form>

</body>
</html>