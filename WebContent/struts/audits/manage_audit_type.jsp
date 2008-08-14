<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects,dragdrop,controls" type="text/javascript"></script>
<style type="text/css">
#list {
	margin:0;
	margin-top:10px;
	padding:0;
	list-style-type: none;
	width:350px;
}
#list li {
	margin:0;
	margin-bottom:4px;
	padding:5px;
	border:1px solid #888;
	cursor:move;
}
</style>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
<tr><td style="vertical-align: top">

<s:form id="save">
<s:hidden name="id"></s:hidden>
<table class="forms">
<tr>
<th>ID:</th>
<td><s:if test="auditType.auditTypeID > 0"><s:property value="auditType.auditTypeID" /></s:if>
<s:else>NEW</s:else></td>
</tr>
<tr>
<th>Audit Type Name:</th>
<td><s:textfield name="auditType.auditName"></s:textfield></td>
</tr>
<tr>
<th>Description:</th>
<td><s:textarea name="auditType.description" rows="2" cols="30" /></td>
</tr>
<tr>
<th>Has Multiple:</th>
<td><s:checkbox name="auditType.hasMultiple" /></td>
</tr>
<tr>
<th>Is Scheduled:</th>
<td><s:checkbox name="auditType.scheduled" /></td>
</tr>
<tr>
<th>Has Auditor:</th>
<td><s:checkbox name="auditType.hasAuditor" /></td>
</tr>
<tr>
<th>Has Requirements:</th>
<td><s:checkbox name="auditType.hasRequirements" /></td>
</tr>
<tr>
<th>Contractor Can View:</th>
<td><s:checkbox name="auditType.canContractorView" /></td>
</tr>
<tr>
<th>Contractor Can Edit:</th>
<td><s:checkbox name="auditType.canContractorEdit" /></td>
</tr>
<tr>
<th>Months to Expire:</th>
<td><s:textfield name="auditType.monthsToExpire" /></td>
</tr>
<tr>
<th>Date to Expire:</th>
<td><s:textfield name="auditType.dateToExpire" /> m/d/yy</td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="auditType.displayOrder" /></td>
</tr>
</table>
<div class="buttons">
	<button class="positive" name="button" type="submit" value="save">Save</button>
<s:if test="auditType.categories.size == 0">
	<button name="button" type="submit" value="delete">Delete</button>
</s:if>
</div>
</s:form>
</td>
<s:if test="id != 0">
<td style="vertical-align: top">
<div>
	<ul id="list">
	<s:iterator value="auditType.categories">
	    <li id="item_<s:property value="id"/>" title="Drag and drop to change order"><s:property value="number"/>. <a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="category"/></a></li>
	</s:iterator>
	</ul>
	
	<a href="ManageCategory.action?button=AddNew&parentID=<s:property value="auditType.auditTypeID"/>&category.auditType.auditTypeID=<s:property value="auditType.auditTypeID"/>">Add New Category</a>
	<script type="text/javascript">
	//<![CDATA[
	Sortable.create("list", 
		{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="auditType.auditTypeID"></s:property>&type=AuditType', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})
	//]]>
	</script>
	<div id="list-info"></div>
</div>
<div id="info">Drag and drop categories to change their order</div>
<br clear="all" />
</td>
</s:if>
</tr>
</table>
</body>
</html>