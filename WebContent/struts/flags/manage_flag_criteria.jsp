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
			aaSorting: [[1, 'asc']],
			aoColumns: [
			            {bVisible: false},
			            {bVisible: false},
			            null,
			            null,
			            null
						],
			iDisplayLength: 25,
			bStateSave: true,
			bAutoWidth: false,
			fnRowCallback: function( nRow, aData, iDisplayIndex ) {
				$(nRow).not('.clickable').attr('id','criteria_'+aData[0]).addClass('clickable').click(function() {
						show(aData[0]);
					});
				return nRow;
			},
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

	<table id="criterialist" class="report" style="width:100%; margin-bottom: 0;">
		<thead>
			<tr>
				<th>ID</th>
				<th>Display Order</th>
				<th>Category</th>
				<th>Label</th>
				<th>Description</th>
			</tr>
		</thead>
		<s:iterator value="criteriaList">
			<tr>
				<td><s:property value="id"/></td>
				<td><s:property value="displayOrder"/></td>
				<td><s:property value="category"/></td>
				<td><s:property value="label"/></td>
				<td><s:property value="description"/></td>
			</tr>
		</s:iterator>
	</table>
</div>

<div id="item">
	<s:include value="manage_flag_criteria_ajax.jsp"/>
</div>

</body>
</html>
