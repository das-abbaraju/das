<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript" src="js/jquery/mcdropdown/jquery.mcdropdown.min.js"></script>
<link rel="stylesheet" href="js/jquery/mcdropdown/css/jquery.mcdropdown.css"/>

<script type="text/javscript" src="js/jquery/jquery.bgiframe.min.js"></script>

<script type="text/javascript">
	var dtable;
	var ddaudit;
	var ddquestion;
	var dialog;
	var selectedaudit;
	var selectedquestion;

	function show(id) {
		if (id !== undefined) {
			$.getJSON('ManageFlagCriteriaAjax.action', 
					{'criteria.id': id, button: 'load'}, 
					function(data, result) {
						$.each(data, function (i,v) {
							if (v == null)
								v = "";
							$('form#itemform [name=criteria.'+i+']').val(v);
						});
						selectedaudit = data['auditType.id'];
						selectedquestion = data['question.id'];
	
						dialog.dialog('open');
					}
			);
		} else {
			$('form#itemform input, form textarea').val('');
			console.log($('form#itemform input, form textarea'));
			selectedaudit = '';
			selectquestion = '';
			dialog.dialog('open');
		}
	}

	$(function() {
		dtable = $('table#criterialist').dataTable({
			bJQueryUI: true, 
			sPaginationType: "full_numbers"
		});

		dialog = $('#item').dialog({
			title: 'Edit Flag Criteria',
			width: '50%',
			modal: true,
			autoOpen: false,
			open: function() {
				if(ddaudit == null) {
					$('#audittype').mcDropdown('#audittypemenu');
					ddaudit = $('#audittype').mcDropdown();
				}
				if(ddquestion == null) {
					$('#question').mcDropdown('#questionmenu');
					ddquestion = $('#question').mcDropdown();
				}
				ddaudit.setValue(selectedaudit);
				ddquestion.setValue(selectedquestion);
			},
			close: function() {
				ddaudit.closeMenu();
				ddquestion.closeMenu();
			},
			buttons: {
				'Save': function() {
					var pars = $('form#itemform').serialize();
					pars += '&button=save';
					var criteria_dialog = $(this);
					$.getJSON('ManageFlagCriteriaAjax.action',
							pars,
							function(data, result) {
								if (data.gritter)
									$.gritter.add(data.gritter);
								if (data.result == 'success') {
									$.each(data.data, function (i,v) {
										if (v == null) v = "";
										$('#criteria_'+data.data.id+' .criteria_'+i).html(v);
										criteria_dialog.dialog('close');
									});
								}
							}
					);
				},
				'Delete': function() {
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
	width: 60%;
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
			<th>Category</th>
			<th>Label</th>
			<th>Description</th>
		</tr>
	</thead>
	<s:iterator value="criteriaList">
		<tr id="criteria_<s:property value="id"/>" onclick="show(<s:property value="id"/>)" class="clickable">
			<td class="criteria_category"><nobr><s:property value="category"/></nobr></td>
			<td class="criteria_label"><nobr><s:property value="label"/></nobr></td>
			<td class="criteria_description"><s:property value="description"/></td>
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
					<label>Description:</label>
					<s:textarea name="criteria.description" cols="30" rows="4"/>
				</li>
				
				<li>
					<label>Comparison:</label>
					<s:select name="criteria.comparison" list="comparisonList" headerKey="" headerValue=" - Comparison - "/>
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
					<div class="mcdropdown_wrapper">
						<s:textfield id="audittype" name="criteria.auditType.id"/>
						<ul id="audittypemenu" class="mcdropdown_menu">
						<s:iterator value="auditTypeMap">
							<li>
								<s:property value="key"/>
								<ul>
									<s:iterator value="value">
										<li rel="<s:property value="id"/>"><s:property value="auditName"/></li>
									</s:iterator>
								</ul>
							</li>
						</s:iterator>
						</ul>
					</div>
				</li>
				<li>
					<label>Question:</label>
					<div class="mcdropdown_wrapper">
						<s:textfield id="question" name="criteria.question.id"/>
						<ul id="questionmenu" class="mcdropdown_menu">
						<s:iterator value="flagQuestionMap" id="atypeclass">
							<li>
								<s:property value="#atypeclass.key"/>
								<ul>
									<s:iterator value="#atypeclass.value" id="atype">
										<li>
											<s:property value="#atype.key.auditName"/>
											<ul>
												<s:iterator value="#atype.value" id="auditcategory">
													<li>
														<s:property value="#auditcategory.key.number"/>. <s:property value="#auditcategory.key.category"/>
														<ul>
															<s:iterator value="#auditcategory.value">
																<li>
																	<s:property value="key.number"/>. <s:property value="key.subCategory"/>
																	<ul>
																		<s:iterator value="value">
																			<li rel="<s:property value="id"/>">
																				<s:property value="number"/>. <s:property value="question"/>
																			</li>
																		</s:iterator>
																	</ul>
																</li>
															</s:iterator>
														</ul>
													</li>
												</s:iterator>
											</ul>
										</li>
									</s:iterator>
								</ul>
							</li>
						</s:iterator>
						</ul>
					</div>
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
					<s:select name="criteria.multiYearScope" list="multiYearScopeList" listValue="description" headerKey="" headerValue=" - Multi Year Scope - "/>
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
