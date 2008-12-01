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
		<li><label>CSR:</label>
			<strong><s:property value="contractor.auditor.name" /></strong>
		</li>
		<li><label>Risk Level:</label>
			<strong><s:property value="contractor.riskLevel" /></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label># of Employees:</label>
			<strong><s:property value="infoSection[69].answer"/></strong>
		</li>
		<li><label>Total Revenue:</label>
			<strong><s:property value="infoSection[1616].answer"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>SIC:</label>
			<strong><s:property value="infoSection[55].answer"/></strong>
		</li>
		<li><label>NAIC:</label>
			<strong><s:property value="infoSection[57].answer"/></strong>
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