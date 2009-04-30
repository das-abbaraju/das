function showCriteria(questionID) {
	startThinking( {
		div :'criteriaEdit',
		type :'large',
		message :"getting Flag Criteria from server..."
	});
	$('criteriaEdit').show();
	new Effect.Opacity('criteriaList', {
		to :0.5,
		duration :0.7
	});

	var pars = {
		'id' :opID,
		'question.id' :questionID
	};
	var myAjax = new Ajax.Updater('criteriaEdit',
			'FlagCriteriaActionAjax.action', {
				method :'post',
				parameters :pars
			});
}

function clearRow(row) {
	$(row+'_comparison').value = '';
	$(row+'_value').value = '';
}

function closeCriteriaEdit() {
	$('criteriaEdit').innerHTML = '';
	$('criteriaEdit').hide();
	new Effect.Opacity('criteriaList', {
		to :1.0,
		duration :0.3
	});
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
					stopThinking();
					refreshList();
					growl();
					closeCriteriaEdit();
				}
		});
}

function growl() {
	var growlBox = $('growlBox');
	Effect.Appear(growlBox, { duration: 1});
	Effect.Fade(growlBox, {delay: 3, duration: 1});
	
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