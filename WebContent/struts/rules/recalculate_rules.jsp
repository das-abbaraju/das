<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:if test="categoryRule">Category </s:if><s:else>Audit Type </s:else>Rule Editor</title>
<link rel="stylesheet" href="css/reports.css?v=${version}" />
<link rel="stylesheet" href="css/forms.css?v=${version}" />
</head>
<body>
<h1>Recalculate Rules</h1>
<s:include value="../../actionMessages.jsp"/>
<s:if test="permissions.canEditCategoryRules">
	<a class="refresh" href="?button=category" onclick="return confirm('Are you sure? This will take awhile');">Recalculate Category Rules Priority</a>
</s:if>
<br />
<s:if test="permissions.canEditAuditRules">
	<a class="refresh" href="?button=auditType" onclick="return confirm('Are you sure? This will take awhile');">Recalculate Audit Type Rules Priority</a>
</s:if>
</body>
</html>