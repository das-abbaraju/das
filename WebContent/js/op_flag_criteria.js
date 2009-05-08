var cal2;
function showCriteria(questionID, auditName, requiresCal) {
	if(requiresCal) {
		if (typeof(cal2) == 'undefined') {
			cal2 = new CalendarPopup('caldiv2');
			cal2.setCssPrefix("PICS");
			cal2.setReturnFunction("calendarReturn");
			cal2.offsetX = -350;
			cal2.offsetY = -20;
		}
	}
	var pars = {
		'id' :opID,
		'question.id' :questionID
	};
	
	Modalbox.show('FlagCriteriaActionAjax.action', {
		method : 'post', 
		params: pars, 
		title: 'Edit Criteria - '+auditName,
		slideDownDuration: .5,
		slideUpDuration: 0,
		resizeDuration: .2,
		overlayClose: false,
		height: 200
		});
}

function showOshaCriteria(type) {
	var pars = {
		'id' :opID,
		'type': type 
	};
	
	Modalbox.show('FlagOshaCriteriaActionAjax.action', {
		method : 'post', 
		params: pars, 
		title: 'Edit Criteria - OSHA',
		slideDownDuration: .5,
		slideUpDuration: 0,
		resizeDuration: .2,
		overlayClose: false,
		height: 200});
}

function calendarReturn(y, m, d) {
	if (window.CP_targetInput != null) {
		var dt = new Date(y, m - 1, d, 0, 0, 0);
		if (window.CP_calendarObject != null) {
			window.CP_calendarObject.copyMonthNamesToWindow();
		} 
		var now = new Date();
		if (typeof(y) == "string" && now.getFullYear() == dt.getFullYear() && now.getMonth() == dt.getMonth() && now.getDay() == dt.getDay())
			window.CP_targetInput.value = "Today";
		else
			window.CP_targetInput.value = formatDate(dt, window.CP_dateFormat);
		
		window.CP_targetInput.onchange();
	}
}

function clearRow(row) {
	$(row+'_comparison').value = '';
	$(row+'_value').value = '';
	$(row+'_clear').hide();
}

function testCriteria(criteria) {
	if (!$('test').value.blank()) {
		startThinking({div:'test_output', message:''});
		var pars = $('criteriaEditForm').serialize(true);
		pars.button = 'test';
		pars.testValue = $('test').value.capitalize();
		var myAjax = new Ajax.Updater('test_output','FlagCriteriaActionAjax.action', {
				method: 'post',
				parameters: pars
			});
	}
}

function closeCriteriaEdit() {
	Modalbox.hide();
}

function deactivateModal() {
	Modalbox.show("<div style='text-align:center'><img src='images/ajax_process2.gif' /></div>");
	Modalbox.deactivate();
}

function saveCriteria(questionID) {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = $('criteriaEditForm').serialize(true);
	pars.button = 'save';
	deactivateModal();
	var myAjax = new Ajax.Updater('growlBox','FlagCriteriaActionAjax.action', {
			method: 'post',
			parameters: pars,
			onComplete: function() {
					location.reload();
				}
		});
}

function saveOshaCriteria() {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = $('criteriaEditForm').serialize(true);
	pars.button = 'save';
	deactivateModal();
	var myAjax = new Ajax.Updater('growlBox','FlagOshaCriteriaActionAjax.action', {
			method: 'post',
			parameters: pars,
			onComplete: function() {
					closeCriteriaEdit();
					stopThinking();
					refreshList();
				}
		});
}

function refreshList() {
	startThinking( {
		message :"refreshing list..."
	});
	$('criteriaList').show();
	var pars = { id : opID, classType: classType };
	var myAjax = new Ajax.Updater('criteriaList',
			'OperatorFlagCriteriaAjax.action', {
				method :'post',
				parameters: pars,
				onComplete : function() {
					stopThinking();
				}
			});
}

function toggleQuestionList() {
	$('questionList').toggle();
	$('addQuestionButton').toggle();
	$('hideQuestionButton').toggle();
}

function questionSearch() {
	startThinking( {
		div :'questionList',
		type :'large',
		message :"searching for matching questions..."
	});

	var box = $('questionTextBox');
	var pars = 'questionName=' + escape(box.value);
	var myAjax = new Ajax.Updater('questionList',
			'QuestionSelectTableAjax.action', {
				method :'post',
				parameters :pars
			});
}

function showNewCriteria() {
	$('criteriaAdd').show();
	Effect.DropOut('addButton');
	$('questionTextBox').focus();
	$('questionList').show();
}

function closeNewCriteria() {
	$('addButton').show();
	$('criteriaAdd').hide();
	$('questionList').hide();
}

function showOtherAccounts() {
	if ($('otherAccounts').visible())
		Effect.SlideUp('otherAccounts', { duration: .2 });
	else
		Effect.SlideDown('otherAccounts', { duration: .3 });
}
