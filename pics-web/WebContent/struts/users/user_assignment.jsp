<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="type"/> Assignment Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js?v=${version}"></script>
<script type="text/javascript" src="js/ReportSearch.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
$(function() {
	$('#assignments').delegate('tr:not(.new) a.remove', 'click', function(e) {
		e.preventDefault();
		if (confirm('Are you sure you want to delete this row?')) {
			var me = $(this);
			var pars = me.closest('tr').find(':input').serialize();
			me.closest('tr').hide();
			$.post('<s:property value="type"/>AssignmentMatrix!remove.action', pars, function(json, status, xhr) {
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
		var clone = $('#clone').clone().removeAttr('id').addClass('new');
		$('#assignments').append(clone);
		if ($(window).scrollTop() + $(window).height() < clone.offset().top)
			$.scrollTo(clone, 800, {axis: 'y'});
		clone.find(':input:first').focus();
	});
	$('a.save').live('click', function(e) {
		e.preventDefault();
		var me = $(this);
		var pars = me.closest('tr').find(':input').serialize();
		if (me.closest('tr').is('.new')) {
			me.closest('tr').find('.ac_text,.ac_hidden').each(function(){
				$(this).removeAttr('id');
			});
		}
		$.post('<s:property value="type"/>AssignmentMatrix!save.action', pars, function(json, status, xhr) {
			if(json.gritter) {
				$.gritter.add({title: json.gritter.title, text: json.gritter.text});
			}
			if (json.status=='success') {
				me.closest('tr').removeClass('dirty');
				if (me.closest('.new').length) {
					me.closest('tr').removeClass('new').find(':input[name="assignment"]').val(json.assignment.id);
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
<h1><s:property value="type"/> Assignment</h1>

<s:if test="type == 'Auditors'">
	<div id="report_data">
		<s:include value="user_assignment_data.jsp"></s:include>
	</div>
</s:if>

<h3><s:property value="type"/> Assignment Matrix</h3>

<s:if test="type.toString() == 'CSR'">
	<div class="right">
		<a 
			class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="javascript: download('CSRAssignmentMatrix');" 
			title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a>
	</div>
</s:if>

<a href="#" class="add">Add New Assignment</a>
	
<table class="report" id="assignments">
	<thead>
		<tr>
			<th>User</th>
			<th><s:text name="Country" /></th>
			<th><s:text name="CountrySubdivision" /></th>
			<th>Zip Start</th>
			<th>Zip End</th>
			<th>Contractor</th>
			<s:if test="type.toString() == 'Auditor'">
				<th>Audit Type</th>
			</s:if>
			<s:if test="type.toString() == 'CSR'">
				<th>Contractor Status</th>
			</s:if>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="assignments">
			<tr>
				<td><s:select list="users" listKey="id" listValue="name" headerKey="0" headerValue="- User -" name="assignment.user" value="%{user.id}"></s:select></td>
				<td><s:select list="countries" listKey="isoCode" listValue="name" headerKey="" headerValue="- Country -" name="assignment.country" value="%{country.isoCode}"></s:select></td>
				<td><s:select list="countrySubdivisions" listKey="isoCode" listValue="name" headerKey="" headerValue="- countrySubdivision -" name="assignment.countrySubdivision" value="%{countrySubdivision.isoCode}"></s:select></td>
				<td><s:textfield name="assignment.postalStart" value="%{postalStart}" size="10"/></td>
				<td><s:textfield name="assignment.postalEnd" value="%{postalEnd}" size="10"/></td>
				<td><pics:autocomplete action="ContractorAutocomplete" htmlName="assignment.contractor" value="contractor" /></td>
				<s:if test="type.toString() == 'Auditor'">
					<td><s:select list="#{2:'Manual Audit',3:'Implementation Audit',100:'HSE Competency Review',176:'WA State Verification'}" headerKey="" headerValue="- Audit Type -" name="auditTypeID" value="%{auditType.id}" /></td>
				</s:if>
				<s:if test="type.toString() == 'CSR'">
					<td><s:property value="%{contractor.status}" /></td>
				</s:if>
				<td><s:hidden name="assignment" value="%{id}"/><a href="#" class="remove"></a><a href="#" class="save"></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>


<table class="hide">
	<tr id="clone">
		<td><s:select list="users" listKey = "id" listValue="name" headerKey="0" headerValue="- User -" name="assignment.user"></s:select></td>
		<td><s:select list="countries" listKey="isoCode" listValue="name" headerKey="" headerValue="- Country -" name="assignment.country"></s:select></td>
		<td><s:select list="countrySubdivisions" listKey="isoCode" listValue="name" headerKey="" headerValue="- countrySubdivision -" name="assignment.countrySubdivision"></s:select></td>
		<td><s:textfield name="assignment.postalStart" size="10"/></td>
		<td><s:textfield name="assignment.postalEnd" size="10"/></td>
		<td><pics:autocomplete action="ContractorAutocomplete" htmlName="assignment.contractor" /></td>
		<s:if test="type.toString() == 'Auditor'">
			<td><s:select list="#{2:'Manual Audit',3:'Implementation Audit',100:'HSE Competency Review',176:'WA State Verification'}" headerKey="" headerValue="- Audit Type -" name="auditTypeID" /></td>
		</s:if>
		<s:if test="type.toString() == 'CSR'">
			<td><s:property value="%{contractor.status}" /></td>
		</s:if>
		<td><s:hidden name="assignment" value=""/><a href="#" class="remove"></a><a href="#" class="save"></a></td>
	</tr>
</table>
</body>
</html>