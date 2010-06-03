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

<script type="text/javscript" src="js/jquery/jquery.bgiframe.min.js"></script>

<script type="text/javascript">
	var dtable;
	var dialog;
	var newItem = false;

	function loadDialog(criteria) {
		$.each(criteria, function (i,v) {
			if (v == null)
				v = "";

			if ((i == 'auditType' || i == 'question') && v != null) 
				$('form#itemform [name='+i+'.id]').val(v.id);
			else if (v === true)
				$('form#itemform [name=criteria.'+i+']').attr('checked', 'checked');
			else if (v === false)
				$('form#itemform [name=criteria.'+i+']').removeAttr('checked');
			else
				$('form#itemform [name=criteria.'+i+']').val(v);
		});
	}

	function show(id) {
		if (id !== undefined) {
			$.getJSON('ManageFlagCriteriaAjax.action', 
					{'criteria.id': id, button: 'load'}, 
					function(data, result) {
						if (data.result == 'success') {
							loadDialog(data.criteria);
							newItem = false;
							dialog.dialog('open');
						} else {
							if (data.gritter) {
								$.gritter.add(data.gritter);
							}
						}
					}
			);
		} else {
			$('form#itemform :input, form#itemform input[name=criteria.id]').val('');
			$('form#itemform :checked').removeAttr('checked');
			newItem = true;
			dialog.dialog('open');
		}
	}

	$(function() {
		dtable = $('table#criterialist').dataTable({
			aaSorting: [[1, 'asc']],
			aoColumns: [
			            {bVisible: false},
			            {bVisible: false},
			            null,
			            null,
			            null
						],
			iDisplayLength: 25,
			bJQueryUI: true,
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

		dialog = $('#item').dialog({
			title: 'Edit Flag Criteria',
			width: '50%',
			modal: true,
			autoOpen: false,
			buttons: {
				'Save': function() {
					var pars = $('form#itemform :input[name][value]').serialize();
					pars += '&button=save';
					var criteria_dialog = $(this);
					$.getJSON('ManageFlagCriteriaAjax.action',
							pars,
							function(data, result) {
								if (data.gritter)
									$.gritter.add(data.gritter);
								if (data.result == 'success') {
									if (newItem) {
										dtable.fnAddData([data.criteria.id, data.criteria.displayOrder, data.criteria.category, data.criteria.label, data.criteria.description]);
									} else {
										dtable.fnUpdate([data.criteria.id, data.criteria.displayOrder, data.criteria.category, data.criteria.label, data.criteria.description], $('#criteria_'+data.criteria.id)[0])
									}
									criteria_dialog.dialog('close');
								} else {
									loadDialog(data.criteria);
								}
							}
					);
				},
				'Delete': function() {
					var criteria_dialog = $(this);
					var pars = $('form#itemform :input[name][value]').serialize();
					pars += "&button=delete";
					$.getJSON('ManageFlagCriteriaAjax.action',
							pars,
							function(data, result) {
								if (data.gritter)
									$.gritter.add(data.gritter);

								if (data.result == 'success') {
									criteria_dialog.dialog('close');
									dtable.fnDeleteRow($('tr#criteria_'+data.id)[0]);
								}
							}
						);
				},
				'Cancel': function() {
					$(this).dialog('close');
				}
			}
		});
	});
</script>

<style>
#item input[type=text], #item textarea {
	font-size: normal;
}
.mcdropdown_wrapper {
	clear: left;
	padding-top: 10px;
}
.mcdropdown_wrapper input {
	font-size: 12px !important;
}
</style>
</head>
<body>
<h1>Manage Flag Criteria</h1>

<div>
<input type="button" class="picsbutton positive add" onclick="show()" value="New Criteria"/>
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
			<td><nobr><s:property value="category"/></nobr></td>
			<td><nobr><s:property value="label"/></nobr></td>
			<td><s:property value="description"/></td>
		</tr>
	</s:iterator>
</table>

<div id="item">
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
					<label>Display Order:</label>
					<s:textfield name="criteria.displayOrder"/>
				</li>
				<li>
					<label>Description:</label>
					<s:textarea name="criteria.description" cols="30" rows="4"/>
				</li>
				
				<li>
					<label>Data Type:</label>
					<s:select name="criteria.dataType" list="datatypeList"/>
				</li>
				<li>
					<label>Comparison:</label>
					<s:select name="criteria.comparison" list="comparisonList"/>
				</li>
				<li>
					<label>Default Hurdle:</label>
					<s:textfield name="criteria.defaultValue"/>
				</li>
				<li>
					<label>Allow Custom Hurdle:</label>
					<s:checkbox name="criteria.allowCustomValue"/> <br/> <br/>
				</li>
				
				<li>
					<label>Audit Type:</label>
					<s:select name="auditType.id" list="{}" headerKey="" headerValue=" - Audit Type - " >
						<s:iterator value="auditTypeMap" var="aType">
							<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
						</s:iterator>
					</s:select>
				</li>
				<li>
					<label>Question:</label>
					<s:select name="question.id" list="{}" headerKey="" headerValue=" - Question - ">
						<s:iterator value="questionMap" var="flagQuestion">
							<s:optgroup label="%{#flagQuestion.key.auditName}" list="#flagQuestion.value" listKey="id" listValue="shortQuestion" />
						</s:iterator>
					</s:select>
				</li>
				
				<li>
					<label>Osha Type:</label>
					<s:select name="criteria.oshaType" list="@com.picsauditing.jpa.entities.OshaType@values()" headerKey="" headerValue=" - Osha Type - "/>
				</li>
				<li>
					<label>Osha Rate Type:</label>
					<s:select name="criteria.oshaRateType" list="@com.picsauditing.jpa.entities.OshaRateType@values()" listValue="description" headerKey="" headerValue=" - Osha Rate Type - "/>
				</li>
				<li>
					<label>Multi Year Scope:</label>
					<s:select name="criteria.multiYearScope" list="scopeList" listValue="description" headerKey="" headerValue=" - Multi Year Scope - "/>
				</li>
				
				<li>
					<label>Validation Required:</label>
					<s:checkbox name="criteria.validationRequired"/>
				</li>
				<li>
					<label>Insurance Criteria:</label>
					<s:checkbox name="criteria.insurance"/>
				</li>
			</ol>
		</fieldset>
	</form>
</div>

</body>
</html>
