<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">auditID = '<s:property value="conAudit.id"/>';</script>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>
<div class="right" id="modes">
	<s:if test="canEditAudit">
		<a class="edit modeset" href="#mode=Edit">Edit</a>
	</s:if>
	<a class="view modeset" href="#mode=View">View</a>
	<s:if test="canVerifyAudit">
		<a class="verify modeset" href="#mode=Verify">Verify</a> 
	</s:if>
</div>

<table id="audit-layout">
	<tr>
		<td id="auditHeaderSideNav" class="auditHeaderSideNav noprint">
			<s:include value="con_audit_sidebar.jsp"/>
		</td>
		<td>
			<div id="auditViewArea"></div>
		</td>
	</tr>
</table>

<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div class="info">
		<b>Safety Professional Notes:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<br clear="all"/>
</body>
</html>
