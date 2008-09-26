<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Mass Mailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script language="JavaScript">

function addToken(tokens) {
	$('emailDraft').value += " <" + tokens.value + ">";
	tokens.value = 0;
	$('emailDraft').focus();
}

function previewEmail(item) {
	alert(item.value);
	
}

</script>
<style type="text/css">
#emailDraft {
	color: black;
	width: 95%;
	padding: 5px;
}
</style>
</head>
<body>
<h1>Mass Mailer</h1>

<div>
<s:include value="../actionMessages.jsp"></s:include>
<s:form id="form1" method="post">

<table border="1" style="width: 100%;">
	<tr>
		<td rowspan="2">
			Contractors:<br />
			<s:select id="contractors" cssClass="forms" name="contractors" size="40" multiple="true" list="data" listKey="get('id')" listValue="get('name')"
				ondblclick="previewEmail(this);" />
		</td>
		<td>
			<table>
				<tr><td><s:select cssClass="forms" id="emailTemplates" name="emailTemplates" headerKey="0" headerValue="- Pick an existing Email Template -" listKey="id" listValue="subject" list="emailTemplates" /></td>
					<td><s:select cssClass="forms" id="tokens" name="tokens" headerKey="0" headerValue="- Add Field to Email -" listKey="tokenName" listValue="tokenName" list="tokens" onchange="addToken(this);" /></td>
				</tr>
			</table>
			<s:textarea cssClass="forms" id="emailDraft" name="emailDraft" rows="20"></s:textarea>
		</td>
	</tr>
	<tr>
		<td id="previewEmail" valign="top"><div id="info">Double-Click Contractor on Left to Preview Email</div></td>
	</tr>
</table>
	
<div class="buttons">
	<button class="positive" name="button" type="submit">Send Emails</button>
</div>	

</s:form>
</div>
</body>
</html>