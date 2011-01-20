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

	function showTable() {
		$('#item').hide();
		$('#table').show();
	}

	function showForm() {
		$('#item').show();
		$('#table').hide();
	}

	function show(id) {
		var data = {};
		if (id === undefined) {
			id = 0;
			data.button = 'new';
		}
		data.id = id;
		startThinking({div: 'thinking', message: 'Loading criteria...'});
		$('#item').load('ManageFlagCriteriaAjax.action', data, function() {
				stopThinking({div: 'thinking'}); 
				showForm();
			} 
		);
	}

	$(function() {
		$('.goback').live('click', function (event) {
			event.preventDefault();
			showTable();
		});
		
		dtable = $('#criterialist').dataTable({
			iDisplayLength: 25,
			bAutoWidth: false,
			bStateSave: true,
			aaSorting: [[0, 'asc'],[1, 'asc'],[2, 'asc']],
			sPaginationType: "full_numbers"
		});

	});
</script>

<s:if test="criteria != null">
<script type="text/javascript">
$(function() {
	showForm();
})
</script>
</s:if>

<style>
#item {
	display: none;
}
#thinking {
	float: right;	
}
</style>
</head>
<body>
<h1>Manage Flag Criteria</h1>

<div id="table">
<s:if test="criteria == null">
	<s:include value="../actionMessages.jsp"/>
</s:if>
	<div>
	<input type="button" class="picsbutton positive add" onclick="show()" value="New Criteria"/>
	<div id="thinking"></div>
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
				<td><a href="#" onclick="show(<s:property value="id"/>); return false;"><s:property value="description" default="Missing Description" /></a></td>
				<td><s:property value="updatedBy2.name" /></td>
				<td><s:date name="updateDate2" format="MM/dd/yyyy"/></td>
			</tr>
		</s:iterator>
	</table>
</div>

<div id="item">
	<s:include value="manage_flag_criteria_ajax.jsp"/>
</div>

</body>
</html>
