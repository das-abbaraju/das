<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript">
	var dtable;

	$(function() {
		
		dtable = $('#criterialist').dataTable({
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
<h1>Manage Flag Criteria</h1>

<s:include value="../actionMessages.jsp"/>
<div>
	<a href="ManageFlagCriteria!edit.action" class="add">New Criteria</a>
</div>

<table id="criterialist" class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Display Order</th>
			<th>Label</th>
			<th>Description</th>
			<th>Updated</th>
			<th>On</th>
		</tr>
	</thead>
	<s:iterator value="criteriaList">
		<tr>
			<td><s:property value="category"/></td>
			<td><s:property value="displayOrder"/></td>
			<td><s:property value="label"/></td>
			<td>
				<a href="ManageFlagCriteria!edit.action?criteria=<s:property value="id"/>">
					<s:if test="!isStringEmpty(description)">
						<s:property value="description" />
					</s:if>
					<s:else>Description is missing...</s:else>
				</a>
			</td>
			<td><s:property value="updatedBy2.name" /></td>
			<td><s:date name="updateDate2" format="MM/dd/yyyy"/></td>
		</tr>
	</s:iterator>
</table>

</body>
</html>
