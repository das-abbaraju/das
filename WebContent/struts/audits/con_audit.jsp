<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for <s:property value="conAudit.contractorAccount.name" /></title> 
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/audit_data_save.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
	var auditID = '<s:property value="conAudit.id"/>';
	var conID = '<s:property value="id"/>';
	<s:if test="conAudit.categories.size == 0">
	$(function() {
		updateCategoriesNow();
	});
	</s:if>
	$(function() {
		$('a.passAudit').live('click', function() {
			window.location.href = $(this).attr('href') + ($(this).attr('href').indexOf("?") > 0 ? "&" : "?") + "auditID=" + auditID;
			return false;
		});
	});
</script>
</head>
<body>
<s:include value="../audits/audit_catHeader.jsp"/>

<s:if test="policy">
	<a href="PolicyVerification.action?button=getFirst" class="picsbutton">First Policy</a>
	<a href="PolicyVerification.action?button=showNext&auditID=<s:property value="auditID" />" class="picsbutton positive">Next Policy &gt;&gt;</a>
	<br clear="all" />
</s:if>

<div id="submitRemind"></div>

<div class="right noprint" id="modes">
	<s:if test="canEditAudit">
		<a class="edit modeset" href="#mode=Edit">Edit</a>
	</s:if>
	<a class="view modeset" href="#mode=View">View</a>
	<s:if test="canVerifyAudit">
		<a class="verify modeset" href="#mode=Verify">Verify</a> 
	</s:if>
	<span style="display: none;" id="printReqButton"><a class="print" href="javascript:window.print();">Print</a></span>
</div>

<table id="audit-layout">
	<tr>
		<td class="auditHeaderSideNav noprint">
			<div id="auditHeaderSideNav">
				<s:include value="con_audit_sidebar.jsp"/>
			</div>
		</td>
		<td style="width: 100%; height: 100%;">
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
