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


<script type="text/javascript">
	function show(id) {
		alert(id);
	}

	function save() {
		alert($('form#itemform').serialize());
	}

	$(function() {
		$('table#criterialist').dataTable({bJQueryUI : true})		
	});
</script>

</head>
<body>
<h1>Manage Flag Criteria</h1>
<div id="list">
	<table id="criterialist" class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Label</th>
		</tr>
	</thead>
		<s:iterator value="criteriaList">
		<tr onclick="show(<s:property value="id"/>)">
			<td><s:property value="category"/></td>
			<td><s:property value="label"/></td>
		</tr>
		</s:iterator>
	</table>
</div>

<div id="item">
	<form id="itemform">
		<fieldset class="form">
			<legend><span>Flag Criteria</span></legend>
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
					<s:textfield name="criteria.description"/>
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
					<label>Custom Value Allowed:</label>
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
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" value="Save" onclick="save(); return false;"/>
		</fieldset>
	</form>
</div>

</body>
</html>
