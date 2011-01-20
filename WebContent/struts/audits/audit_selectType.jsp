<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Management</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>
<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<script type="text/javascript">
var dtable;
$(function() {
	dtable = $('.report').dataTable({
		"aaSorting": [[2,'asc']],
		"sPaginationType": "full_numbers"
	});
});
</script>
<style type="text/css">
table.report {
	width: 100%;
}
</style>
</head>
<body>
<h1>Audit Management</h1>
<a class="add" href="?button=Add New">Add New Audit Type</a>

<table class="report">
<thead>
<tr>
	<th>Order</th>
	<th>Class</th>
	<th>Audit Name</th>
</tr>
</thead>
<s:iterator value="auditTypes">
<tr>
	<td class="center"><s:property value="displayOrder"/></td>
	<td><s:property value="classType"/></td>
	<td><a title="<s:property value="auditName"/>" href="ManageAuditType.action?id=<s:property value="id"/>"><s:property value="auditName"/></a></td>
</tr>
</s:iterator>
</table>
</body>
</html>
