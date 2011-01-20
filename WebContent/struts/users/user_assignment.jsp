<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>User Assignment Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">
$(function() {
	$('a.remove').live('click', function(e) {
		e.preventDefault();
		alert('removed'); 
	});
	$('a.add').live('click', function(e) {
		e.preventDefault();
		$('#assignments').append($('#clone').clone().removeAttr('id').removeClass('hide').addClass('dirty'));
	});
	$('a.save').live('click', function(e) {
		e.preventDefault();
		alert('saved');
		$(this).closest('tr').addClass('dirty');
	});
	$('#assignments').delegate(':input', 'change', function() {
		$(this).closest('tr').addClass('dirty');
	});
});
</script>
<style>
a.save { display: none; }
tr.dirty a.save { display: inline; }
</style>
</head>
<body>
<h1>User Assignment Matrix</h1>

<table class="report" id="assignments">
	<thead>
		<tr>
			<th>User</th>
			<th>Country</th>
			<th>State</th>
			<th>Zip Start</th>
			<th>Zip End</th>
			<th></th>
		</tr>
	</thead>
	<tr class="hide" id="clone">
		<td><s:select list="#{2357:'Kyle'}" name="assignment.user.id"></s:select></td>
		<td><s:select list="{'us'}" name="assignment.country.isoCode"></s:select></td>
		<td><s:select list="{'ca'}" name="assignment.state.isoCode"></s:select></td>
		<td><s:textfield name="assignment.postalStart" /></td>
		<td><s:textfield name="assignment.postalEnd" /></td>
		<td><a href="#" class="save"></a></td>
	</tr>
	<s:iterator value="assignments" id="assignment">
		<tr>
			<td><s:select list="#{0:'',2357:'Kyle'}" name="#assignment.user.id"></s:select></td>
			<td><s:select list="{'','us'}" name="assignment.country.isoCode"></s:select></td>
			<td><s:select list="{'','ca'}" name="assignment.state.isoCode"></s:select></td>
			<td><s:textfield name="assignment.postalStart" /></td>
			<td><s:textfield name="assignment.postalEnd" /></td>
			<td><a href="#" class="remove"></a><a href="#" class="save"></a></td>
		</tr>
	</s:iterator>
</table>
<a href="#" class="add">Add New Assignment</a>
</body>
</html>