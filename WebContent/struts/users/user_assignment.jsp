<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="type"/> Assignment Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js"></script>
<script type="text/javascript">
$(function() {
	$('#assignments').delegate('tr:not(.new) a.remove', 'click', function(e) {
		e.preventDefault();
		if (confirm('Are you sure you want to delete this row?')) {
			var me = $(this);
			var pars = me.closest('tr').find(':input').serialize() + "&button=Remove";
			me.closest('tr').hide();
			$.post('<s:property value="type"/>AssignmentMatrixJSON.action', pars, function(json, status, xhr) {
				console.log($(json.oldAssigment).serialize());
				if(json.gritter) {
					$.gritter.add({title: json.gritter.title, text: json.gritter.text});
				}
				if (json.status!='success') {
					me.closest('tr').show();
				}
			}, 'json');
		}
	});
	$('#assignments').delegate('tr.new a.remove', 'click', function(e) {
		e.preventDefault();
		$(this).closest('tr').remove();		
	});
	$('a.add').live('click', function(e) {
		e.preventDefault();
		var clone = $('#clone').clone().removeAttr('id').removeClass('hide').addClass('new');
		$('#assignments').append(clone);
		if ($(window).scrollTop() + $(window).height() < clone.offset().top)
			$.scrollTo(clone, 800, {axis: 'y'});
		clone.find(':input:first').focus();
	});
	$('a.save').live('click', function(e) {
		e.preventDefault();
		var me = $(this);
		var pars = me.closest('tr').find(':input').serialize() + "&button=Save";
		$.post('<s:property value="type"/>AssignmentMatrixJSON.action', pars, function(json, status, xhr) {
			if(json.gritter) {
				$.gritter.add({title: json.gritter.title, text: json.gritter.text});
			}
			if (json.status=='success') {
				me.closest('tr').removeClass('dirty');
				if (me.closest('.new').length) {
					me.closest('tr').removeClass('new').find(':input[name=assignment]').val(json.assignment.id);
				}
			}
		}, 'json');
	});
	$('#assignments').delegate(':not(.dirty) :input', 'keypress change', function(e) {
		$(this).closest('tr').addClass('dirty');
	});
	$('#assignments').delegate(':input', 'keypress', function(e) {
		var code = e.keyCode || e.which;
		if (code == 13)
			$(this).closest('tr.dirty').find('a.save').trigger('click');
	});

	$('body:not(.busy)').live('keypress', function(e) {
		var code = e.keycode || e.which;
		// create = c
		if (code == 99 || code == 67) {
			$('a.add').trigger('click');
			e.preventDefault();
		}
	});
	
	$('body').delegate(':input', 'focus', function(e) {
		$('body').addClass('busy');
	}).delegate(':input', 'blur', function(e) {
		$('body').removeClass('busy');
	}).delegate(':input', 'keydown', function(e) {
		var code = e.keycode || e.which;
		if (code == 27)
			$(this).blur();
	});
	
	if(!$('#assignments tbody tr:not(.hide)').length) {
		$('a.add').trigger('click');
	}
	
});
</script>
<style>
a.save { display: none; }
tr.dirty a.save { display: inline; }
</style>
</head>
<body>
<h1><s:property value="type"/> Assignment Matrix</h1>

<a href="#" class="add">Add New Assignment</a>
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
	<tbody>
		<tr class="hide" id="clone">
			<td><s:select list="users" listKey = "id" listValue="name" headerKey="0" headerValue="- User -" name="assignment.user"></s:select></td>
			<td><s:select list="countries" listKey="isoCode" listValue="name" headerKey="" headerValue="- Country -" name="assignment.country"></s:select></td>
			<td><s:select list="states" listKey="isoCode" listValue="name" headerKey="" headerValue="- State -" name="assignment.state"></s:select></td>
			<td><s:textfield name="assignment.postalStart" /></td>
			<td><s:textfield name="assignment.postalEnd" /></td>
			<td><s:hidden name="assignment" value=""/><a href="#" class="remove"></a><a href="#" class="save"></a></td>
		</tr>
		<s:iterator value="assignments">
			<tr>
				<td><s:select list="users" listKey="id" listValue="name" headerKey="0" headerValue="- User -" name="assignment.user" value="%{user.id}"></s:select></td>
				<td><s:select list="countries" listKey="isoCode" listValue="name" headerKey="" headerValue="- Country -" name="assignment.country" value="%{country.isoCode}"></s:select></td>
				<td><s:select list="states" listKey="isoCode" listValue="name" headerKey="" headerValue="- State -" name="assignment.state" value="%{state.isoCode}"></s:select></td>
				<td><s:textfield name="assignment.postalStart" value="%{postalStart}" /></td>
				<td><s:textfield name="assignment.postalEnd" value="%{postalEnd}" /></td>
				<td><s:hidden name="assignment" value="%{id}"/><a href="#" class="remove"></a><a href="#" class="save"></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>