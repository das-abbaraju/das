function showCriteria(questionID, auditName) {
	var pars = {
		'id' :opID,
		'question.id' :questionID
	};
	
	Modalbox.show('FlagCriteriaActionAjax.action', {method : 'post', params: pars, title: 'Edit Criteria - '+auditName});
}

function clearRow(row) {
	$(row+'_comparison').value = '';
	$(row+'_value').value = '';
}

function closeCriteriaEdit() {
	Modalbox.hide();
}

function saveCriteria(questionID) {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = $('criteriaEditForm').serialize(true);
	pars.button = 'save';
	var myAjax = new Ajax.Updater('growlBox','FlagCriteriaActionAjax.action', {
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
	var pars = { id : opID };
	var myAjax = new Ajax.Updater('criteriaList',
			'OperatorFlagCriteriaAjax.action', {
				method :'post',
				parameters: pars,
				onComplete : function() {
					stopThinking();
				}
			});
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