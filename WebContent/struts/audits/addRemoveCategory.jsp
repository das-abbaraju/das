<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Add/Remove Categories on <s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
auditID = '<s:property value="conAudit.id"/>';
$(function(){
	$('.addCat').live('click', function(){
		var cat = $(this).attr('id');
		$('#categoryArea').load('AddRemoveCategoriesAjax.action', {button: 'IncludeCategory', 
			auditID: auditID, categoryID: cat.slice(cat.indexOf('_')+1, cat.length)});
	});
	$('.removeCat').live('click', function(){
		var cat = $(this).attr('id');
		$('#categoryArea').load('AddRemoveCategoriesAjax.action', {button: 'UnincludeCategory', 
			auditID: auditID, categoryID: cat.slice(cat.indexOf('_')+1, cat.length)});
	});	
});
</script>
</head>
<body>
<h1>Add/Remove Category <span class="sub">
on <s:property value="conAudit.auditType.auditName" /> for <s:property value="conAudit.contractorAccount.name" />
</span></h1>
<div style="width: 100%">
	<div id="categoryArea">
		<s:include value="addRemoveCategoryData.jsp" />	
	</div>
</div>
</body>
</html>