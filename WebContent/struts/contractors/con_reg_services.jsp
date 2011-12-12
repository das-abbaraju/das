<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ContractorRegistrationServices.title" /></title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />

<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/audit_data_save.js?v=<s:property value="version"/>"></script>

</head>
<body>

<s:if test="!isStringEmpty(output)">
	<div class="alert"><s:property value="output" escape="false" /></div>
</s:if>

<div class="info"><s:text name="ContractorRegistrationServices.Instructions" /></div>

<div id="auditViewArea">
	<s:iterator value="categories" var="catDataEntry">
		<s:set name="category" value="#catDataEntry.key" />
		<s:include value="../audits/audit_cat_view.jsp"/>
	</s:iterator>
</div>
</body>
</html>
