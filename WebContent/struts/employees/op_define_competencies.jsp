<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
fieldset.bottom {
	float:left;
}
#item input[type=text], #item textarea {
	font-size: normal;
}
#item {
	display: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>
<script type="text/javscript" src="js/jquery/jquery.bgiframe.min.js"></script>
<script type="text/javascript">
	var dialog;
	var dtable;
	var newItem = false;

	function loadDialog(competency) {
		$.each(competency, function (i,v) {
			if (v == null)
				v = "";
			if (v === true)
				$('form#itemform [name="competency.'+i+'"]').attr('checked', 'checked');
			else if (v === false)
				$('form#itemform [name="competency.'+i+'"]').removeAttr('checked');
			else
				$('form#itemform [name="competency.'+i+'"]').val(v);
		});
	}

	function show(id) {
		if (id !== undefined) {
			$.post('DefineCompetencies!load.action', 
					{competency: id, operator: '<s:property value="operator.id"/>'}, 
					function(data, result) {
						if (data.result == 'success') {
							loadDialog(data.competency);
							newItem = false;
							dialog.dialog('open');
						} else {
							if (data.gritter) {
								$.gritter.add(data.gritter);
							}
						}
					},
					'json'
			);
		} else {
			$('form#itemform :input').val('');
			$('form#itemform input[name="operator"]').val('<s:property value="operator.id"/>');
			$('form#itemform input[name="competency"]').val(0);
			$('form#itemform :checked').removeAttr('checked');
			newItem = true;
			dialog.dialog('open');
		}
	}

	$(function() {
		dialog = $('#item').dialog({
			title: translate('JS.<s:property value="scope" />.label.EditCompetency'),
			width: '50%',
			modal: true,
			autoOpen: false,
			buttons: {
				'<s:text name="button.Save" />': function() {
					var pars = $('form#itemform :input[name]').serialize();
					var competency_dialog = $(this);
					$.post('DefineCompetencies!save.action',
							pars,
							function(data, result) {
								if (data.gritter)
									$.gritter.add(data.gritter);
								if (data.result == 'success') {
									if (newItem) {
										dtable.fnAddData([data.competency.id, data.competency.category, data.competency.label, data.competency.description, data.competency.editLink]);
									} else {
										dtable.fnUpdate([data.competency.id, data.competency.category, data.competency.label, data.competency.description, data.competency.editLink], $('#competency_'+data.competency.id)[0]);
									}
									competency_dialog.dialog('close');
								} else {
									loadDialog(data.competency);
								}
							},
							'json'
					);
				},
				'<s:text name="button.Cancel" />': function() {
					$(this).dialog('close');
				}
			}
		});

		$('#category_autocomplete').autocomplete('CategorySuggestAjax.action', {minChars: 1});

		dtable = $('#comptable').dataTable({
				aaSorting: [[1, 'asc']],
				aoColumns: [
			            {bVisible: false},
			            null,
			            null,
			            null,
			            null
						],
				bSearch: false,
				bLengthChange: false,
				bInfo: false,
				bAutoWidth: false,
				bPaginate: false,
				fnRowCallback: function( nRow, aData, iDisplayIndex ) {
					$(nRow).not('.marked_up').attr('id','competency_'+aData[0]).addClass('marked_up');
					return nRow;
				}
			});

		$('delay').show("fast");

		$('#comptable_filter').css({'float':'none', 'clear':'both', 'width':'auto'});
		
		$('#addCompetencyLink').live('click', function(e) {
			e.preventDefault();
			show();
		});
		
		$('a.edit').live('click', function(e) {
			e.preventDefault();
			var id = $(this).closest('tr').attr('id').split('_')[1];
			show(id);
		});
	});
</script>
</head>
<body>
	<h1><s:text name="%{scope}.title" /></h1>
	<a href="#" id="addCompetencyLink" class="add"><s:text name="%{scope}.link.AddHSECompetency" /></a>
	<table class="report" id="comptable">
		<thead>
			<tr>
				<th>ID</th>
				<th><s:text name="OperatorCompetency.category" /></th>
				<th><s:text name="OperatorCompetency.label" /></th>
				<th><s:text name="OperatorCompetency.description" /></th>
				<th><s:text name="button.Edit" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="operator.competencies">
				<tr id="comp_<s:property value="id" />">
					<td><s:property value="id" /></td>
					<td><s:property value="category" /></td>
					<td><s:property value="label" /></td>
					<td><s:property value="description" /></td>
					<td class="center"><a href="#" class="edit"></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<div id="item">
		<form id="itemform">
			<s:hidden name="operator" />
			<s:hidden name="competency" />
			<fieldset class="form" style="border: none">
				<ol>
					<li>
						<label><s:text name="OperatorCompetency.category" />:</label>
						<s:textfield maxlength="50" id="category_autocomplete" name="competency.category" />
					</li>
					<li>
						<label><s:text name="OperatorCompetency.label" />:</label>
						<s:textfield name="competency.label" maxlength="15" size="15"/>
					</li>
					<li>
						<label><s:text name="OperatorCompetency.description" />:</label>
						<s:textarea name="competency.description" cols="40" rows="5" />
					</li>
				</ol>
			</fieldset>
		</form>
	</div>
</body>
</html>