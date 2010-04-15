<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Define Competencies</title>
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
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />

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
				$('form#itemform [name=competency.'+i+']').attr('checked', 'checked');
			else if (v === false)
				$('form#itemform [name=competency.'+i+']').removeAttr('checked');
			else
				$('form#itemform [name=competency.'+i+']').val(v);
		});
	}

	function show(id) {
		if (id !== undefined) {
			$.getJSON('DefineCompetenciesAjax.action', 
					{'competency.id': id, 'id': '<s:property value="operator.id"/>', button: 'load'}, 
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
					}
			);
		} else {
			$('form#itemform :input, form#itemform input[name=competency.id]').val('');
			$('form#itemform :checked').removeAttr('checked');
			newItem = true;
			dialog.dialog('open');
		}
	}

	$(function() {
		dialog = $('#item').dialog({
			title: 'Edit Competency',
			width: '50%',
			modal: true,
			autoOpen: false,
			buttons: {
				'Save': function() {
					var pars = $('form#itemform :input[name]').serialize();
					pars += '&button=save';
					var competency_dialog = $(this);
					$.post('DefineCompetenciesAjax.action',
							pars,
							function(data, result) {
								if (data.gritter)
									$.gritter.add(data.gritter);
								if (data.result == 'success') {
									console.log(data);
									if (newItem) {
										dtable.fnAddData([data.competency.id, data.competency.category, data.competency.label, data.competency.description, "0%", data.competency.helpPageLink, data.competency.editLink, data.competency.deleteLink]);
									} else {
										dtable.fnUpdate([data.competency.id, data.competency.category, data.competency.label, data.competency.description, "0%", data.competency.helpPageLink, data.competency.editLink, data.competency.deleteLink], $('#competency_'+data.competency.id)[0]);
									}
									competency_dialog.dialog('close');
								} else {
									loadDialog(data.competency);
								}
							},
							'json'
					);
				},
				'Cancel': function() {
					$(this).dialog('close');
				}
			}
		});

		$('#category_autocomplete').autocomplete('CategorySuggestAjax.action', {minChars: 1});

		dtable = $('#comptable').dataTable({
				aaData: <s:property value="dtable" escape="false"/>,
				aaSorting: [[1, 'asc']],
				aoColumns: [
			            {bVisible: false},
			            null,
			            null,
			            null,
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
	});
</script>
</head>
<body>
<s:include value="../operators/opHeader.jsp"/>

				<a onclick="show(0)"
					href="#" id="addCompetencyLink" class="picsbutton positive">Add Competency</a>

				<s:if test="competencies.size > 0">
					<table class="report" id="comptable">
						<thead>
							<tr>
								<th>ID</th>
								<th>Category</th>
								<th>Label</th>
								<th>Description</th>
								<th>Percent Used</th>
								<th>Help Page</th>
								<th>Edit</th>
								<th>Delete</th>
							</tr>
						</thead>
					</table>
				</s:if>
				<s:else>
					<div class="info">No Competencies Exist. Please Add a Competency.</div>
				</s:else>
				
				<div id="item">
					<form id="itemform">
						<fieldset class="form" style="border: none">
						<s:hidden name="id"/>
						<s:hidden name="competency.id"/>
							<ol>
								<li>
									<label>Category:</label>
									<s:textfield maxlength="50" id="category_autocomplete" name="competency.category" />
								</li>
								<li>
									<label>Label:</label>
									<s:textfield name="competency.label" maxlength="15" size="15"/>
								</li>
								<li>
									<label>Help Page:</label>
									<s:textfield maxlength="100" size="50" name="competency.helpPage" />
								</li>
								<li>
									<label>Description:</label>
									<s:textarea name="competency.description" cols="40" rows="5" />
								</li>
							</ol>
						</fieldset>
					</form>
				</div>
	</body>
</html>