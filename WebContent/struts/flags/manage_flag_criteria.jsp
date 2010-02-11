<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript">
	function show(id) {
		$.getJSON('ManageFlagCriteriaAjax.action', 
				{'criteria.id': id, button: 'load'}, 
				function(data, result) {
					$.each(data, function (i,v) {
						$('form [name=criteria.'+i+']').val(v);
					});
				}
		);
		$('#item').dialog({
			title: 'Edit Flag Criteria',
			width: '50%',
			modal: true,
			close: function() {
				$(this).dialog('destroy');
			},
			buttons: {
				'Save': function() {
				
				},
				'Cancel': function() {
					$(this).dialog('close');
				}
			}
		});
	}

	function save() {
		alert($('form#itemform').serialize());
	}

	$(function() {
		$('table#criterialist').dataTable({bJQueryUI: true, sPaginationType: "full_numbers" });
	});
</script>

<style>
#item input, #item textarea {
	width: 60%;
}
</style>
</head>
<body>
<h1>Manage Flag Criteria</h1>
<table id="criterialist" class="report" style="width:100%; margin-bottom: 0;">
	<thead>
		<tr>
			<th>Category</th>
			<th>Label</th>
			<th>Description</th>
		</tr>
	</thead>
	<s:iterator value="criteriaList">
		<tr onclick="show(<s:property value="id"/>)" class="clickable">
			<td><nobr><s:property value="category"/></nobr></td>
			<td><nobr><s:property value="label"/></nobr></td>
			<td><s:property value="description"/></td>
		</tr>
	</s:iterator>
</table>

<div id="item" style="display:none">
	<form id="itemform">
		<fieldset class="form" style="border: none">
		<s:hidden name="criteria.id"/>
			<ol>
				<li>
					<label>Category:</label>
					<s:textfield name="criteria.category"/>
				</li>
				<li>
					<label>Label:</label>
					<s:textfield name="criteria.label"/>
				</li>
				<li>
					<label>Description:</label>
					<s:textarea name="criteria.description" cols="30" rows="4"/>
				</li>
				
				<li>
					<label>Comparison:</label>
					<s:textfield name="criteria.comparison"/>
				</li>
				<li>
					<label>Default Value:</label>
					<s:textfield name="criteria.defaultValue"/>
				</li>
				<li>
					<label>Data Type:</label>
					<s:textfield name="criteria.dataType"/>
				</li>
				<li>
					<label>Custom Value:</label>
					<s:checkbox name="criteria.allowCustomValue"/>
				</li>
				
				<li>
					<label>Audit Type:</label>
					<s:textfield name="criteria.auditType.id"/>
				</li>
				<li>
					<label>Question:</label>
					<s:textfield name="criteria.question.id"/>
				</li>
				
				<li>
					<label>Osha Type:</label>
					<s:textfield name="criteria.oshaType"/>
				</li>
				<li>
					<label>Osha Rate Type:</label>
					<s:textfield name="criteria.oshaRateType"/>
				</li>
				<li>
					<label>Multi Year Scope:</label>
					<s:textfield name="criteria.multiYearScope"/>
				</li>
				
				<li>
					<label>Validation Required:</label>
					<s:checkbox name="criteria.validationRequired"/>
				</li>
			</ol>
		</fieldset>
	</form>
</div>

</body>
</html>
