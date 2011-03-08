<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{conAudit.auditType.getI18nKey('name')}" /> for <s:property value="conAudit.contractorAccount.name" /></title> 
<link rel="stylesheet" type="text/css" href="css/print.css?v=<s:property value="version"/>" />
</head>
<body>

<a class="print" href="#" onclick="window.print(); return false;"><s:text name="global.print"/></a>

<h1>
	<s:text name="%{conAudit.auditType.getI18nKey('name')}" />
	<s:if test="conAudit.auditFor != null && conAudit.auditFor.length() > 0">for <s:property value="conAudit.auditFor"/></s:if>
	<s:elseif test="!conAudit.auditType.pqf">- <s:date name="conAudit.effectiveDateLabel" format="MMM yyyy" /></s:elseif>
</h1>

<div id="auditViewArea">
	<s:include value="audit_cat_view_ajax.jsp"/>
</div>

</body>
</html>
