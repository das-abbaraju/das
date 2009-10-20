function showCriteria(questionID, auditName) {
	var pars = {
		'id' :opID,
		'question.id' :questionID
	};
	$dialog.load('FlagCriteriaActionAjax.action', pars, 
		function() {
			$dialog.dialog({
				title:'Flag Criteria',
				modal: true,
				width: 500,
				open: function() {
					$dialog.find('.datepicker').datepicker();
					$('.red').change(function(){$('#red_clear').show();});
					$('.amber').change(function(){$('#amber_clear').show();});
				}, 
				close: function() {
					$(this).dialog('destroy');
				},
				buttons: {
					Save: function() {
						saveCriteria();
					},
					Cancel: function() {
						$dialog.dialog('close');
					}
				}
		});
	});
}

function showOshaCriteria(type) {
	var pars = {
		'id' :opID,
		'type': type 
	};
	$dialog.load('FlagOshaCriteriaActionAjax.action', pars, 
		function() {
			$dialog.dialog({
				title:'Flag Criteria',
				modal: true,
				width: 600,
				open: function() {
					$dialog.find('.datepicker').datepicker();
				},
				close: function() {
					$(this).dialog('destroy');
				},
				buttons: {
					Save: function() {
						saveOshaCriteria();
					},
					Cancel: function() {
						$dialog.dialog('close');
					}
				}
		});
	});
}

function clearRow(row) {
	$('.'+row).val('');
	$('#'+row+'_clear').hide();
}

function testCriteria(criteria) {
	if (!$('#test').blank()) {
		startThinking({div:'test_output', message:''});
		var data = {};
		$.each($('#criteriaEditForm').serializeArray(), function(i, e) {
			data[e.name] = e.value;
		});
		data.button = 'test';
		data.testValue = $('#test').capitalize();
		$('#test_output').load('FlagCriteriaActionAjax.action', data);
	}
}

function closeCriteriaEdit() {
	Modalbox.hide();
}

function deactivateModal() {
	Modalbox.show("<div style='text-align:center'><img src='images/ajax_process2.gif' /></div>");
	Modalbox.deactivate();
}

function saveCriteria() {
	var data = {};
	$.each($('#criteriaEditForm').serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	data.button = 'save';
	$.ajax({
		url: 'FlagCriteriaActionAjax.action',
		data: data,
		complete: function() {
			$dialog.dialog('close');
			$tabs.tabs('load', $tabs.tabs('option', 'selected'));
		}
	});
}

function saveOshaCriteria() {
	var data = {};
	$.each($('#criteriaEditForm').serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	data.button = 'save';
	$.ajax({
		url: 'FlagOshaCriteriaActionAjax.action',
		data: data,
		complete: function() {
			$dialog.dialog('close');
		}
	});
}

function getAddQuestions(type) {
	var layer = '#'+type+'_questions';
	if ($(layer).is(':hidden')) {
		var data= {
			id :opID,
			classType: type,
			button: 'questions'
		};
		startThinking({div:type+'_thinking', message:'Fetching questions...'});
		$(layer).load('OperatorFlagCriteriaAjax.action', data, 
			function() {
				$(layer).show("slow");
				stopThinking({div:type+'_thinking'});
			}
		);
	} else {
		$(layer).hide('slow');
	}
}

function showOtherAccounts() {
	$('#otherAccounts').toggle('slow');
}

function showHudleType(elm, criteria) {
	var option =  $('#'+elm).val();
	if(option == 'None') {
		$('#show_'+criteria+'hurdle').hide();
		$('#show_'+criteria+'hurdlepercent').hide();
	}
	if(option == 'Absolute') {
		$('#show_'+criteria+'hurdle').show();
		$('#show_'+criteria+'hurdlepercent').hide();
	}
	if(option == 'NAICS') {
		$('#show_'+criteria+'hurdle').show();
		$('#show_'+criteria+'hurdlepercent').show();
	}	
	return false;
}
