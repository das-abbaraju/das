<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
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
		startThinking( {div: 'loadingArea', message: 'Adding Category' } );
		var cat = $(this).attr('id');
		addRemoveCat(cat, 'IncludeCategory');
	});
	$('.removeCat').live('click', function(){
		startThinking( {div: 'loadingArea', message: 'Removing Category' } );
		var cat = $(this).attr('id');
		addRemoveCat(cat, 'UnincludeCategory');
	});	
	$('.sc_link').live('click',function(){
		var elemID= $(this).attr('id').slice($(this).attr('id').indexOf('_')+1, $(this).attr('id').length);
		if(!$('#sc_'+elemID+':hidden').size()>0)
			$(this).text('Show Subcategories');
		else
			$(this).text('Hide Subcategories');	
		$('#sc_'+elemID).toggle();	
	});
});

function addRemoveCat(cat, action){
	$('#categoryArea').load('AddRemoveCategoriesAjax.action', 
			{button: action, auditID: auditID, categoryID: cat.slice(cat.indexOf('_')+1, cat.length)}, function(){
				stopThinking({div: 'loadingArea'});
				$.gritter.add({
					title:'Adding/Removing categories will take some time to complete',
					text: 'To see the results immediately you must click on recalculate on the audit page'
				});
	});	
}
</script>
</head>
<body>
<h1>Add/Remove Category <span class="sub">
on <s:property value="conAudit.auditType.auditName" /> for <s:property value="conAudit.contractorAccount.name" />
</span></h1>
<a href="Audit.action?auditID=<s:property value="conAudit.id"/>">&lt;&lt; Back to <s:property value="conAudit.auditType.auditName" /> for <s:property value="conAudit.contractorAccount.name" /></a>
<div id="loadingArea"></div>
<div style="width: 100%">
	<div id="categoryArea">
		<s:include value="addRemoveCategoryData.jsp" />	
	</div>
</div>
</body>
</html>