<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Override</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>
	<s:include value="../contractors/conHeader.jsp"/>
	
	<s:include value="../actionMessages.jsp"/>
	<form method="post">
		<s:hidden name="id"/>
		<fieldset class="form">
			<h2 class="formLegend">Audit Override</h2>
			<ol>
				<li><label>Audit Type:</label>
					<s:select name="conAudit.auditType.id" list="overrideAudits" 
							headerKey="" headerValue="- Audit Type -" listKey="id" listValue="auditName" />
				</li>
				<li><label>For:</label>
					<s:textfield name="conAudit.auditFor"/>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit name="button" value="Create" cssClass="picsbutton positive"/>
			<s:submit name="button" value="Create and Stay" cssClass="picsbutton positive"/>
		</fieldset>
	</form>
</body>
</html>
