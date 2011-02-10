<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Operators By Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript">
	var dtable;

	$(function() {
		dtable = $('#criteriaOperators').dataTable({
			iDisplayLength: 25,
			bAutoWidth: false,
			bStateSave: true,
			aaSorting: [[0, 'asc'],[1, 'asc'],[2, 'asc']],
			sPaginationType: "full_numbers"
		});

	});
</script>

</head>
<body>
<h1>Operators By Criteria</h1>

<div id="table">
	<table id="criteriaOperators" class="report">
		<thead>
			<tr>
				<th>Operator Name</th>
				<th>Status</th>
			</tr>
		</thead>
		<s:iterator value="CriteriaOperators">
			<tr>
				<td><s:property value="Operator.FullName"/></td>
				<td><s:property value="Operator.Status"/></td>
			</tr>
		</s:iterator>
	</table>
</div>

</body>
</html>