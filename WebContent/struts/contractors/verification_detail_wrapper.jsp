<%@page language="java" errorPage="exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" /> 
<script src="js/validate_contractor.js" type="text/javascript"></script>
</head>
<body>
<s:include value="conHeader.jsp" />

<div id="auditHeader">
	<fieldset>
	<ul>
		<li><label>Type:</label>
			<s:property value="conAudit.auditType.auditName" />
			 #<s:property value="conAudit.id" />
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>Status:</label>
			<s:property value="conAudit.auditStatus" />
		</li>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>

<div id="verification_detail">
<s:include value="verification_detail.jsp" />
</div>

<div id="verification_audit"></div>
<div class="clear"></div>
</body>
</html>