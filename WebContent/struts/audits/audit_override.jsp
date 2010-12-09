<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Override</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript">
$(function() {
	$('.block').click(function() {
		$.blockUI({message:'Creating Audit...'});
	});
})
</script>
</head>
<body>
	<s:include value="../contractors/conHeader.jsp"/>
	
	<s:include value="../actionMessages.jsp"/>
	<s:if test="overrideAudits.size() > 0">
	<form method="post">
		<s:hidden name="id"/>
		<fieldset class="form">
			<h2 class="formLegend">Audit Override</h2>
			<ol>
				<li><label>Audit Type:</label>
					<s:select name="auditTypeID" list="overrideAudits" 
							headerKey="" headerValue="- Audit Type -" listKey="id" listValue="auditName" />
				</li>
				<s:if test="permissions.admin">
					<li><label>Operator</label>
						<s:select name="requestingOpID" list="contractor.operators"
								headerKey="" headerValue="- Operator -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
					</li>
				</s:if>
				<li><label>For:</label>
					<s:textfield name="auditFor"/>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit name="button" value="Create" cssClass="picsbutton positive block"/>
			<s:submit name="button" value="Create and Stay" cssClass="picsbutton positive"/>
		</fieldset>
	</form>
	</s:if>
	<s:else>
		<div class="info">There are no audits to add.</div>
	</s:else>
</body>
</html>
