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

function closeEditCriteria() {
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
	// TODO save criteriaEditForm
	// alert($('criteriaEditForm').serialize());
	stopThinking();
	refreshList();
	closeEditCriteria();
}

function refreshList() {
	startThinking( {
		message :"refreshing list..."
	});
	$('criteriaList').show();
	var myAjax = new Ajax.Updater('criteriaList',
			'OperatorFlagCriteriaAjax.action', {
				method :'post',
				onComplete : function() {
					stopThinking();
				}
			});
}

function showNewCriteria() {
	$('criteriaAdd').show();
	Effect.DropOut('addButton');
	$('questionTextBox').focus();
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

function closeNewCriteria() {
	$('addButton').show();
	$('criteriaAdd').hide();
}

function saveNewCriteria(questionID) {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = {
		'id' :opID,
		'question.id' :questionID,
		'button' :'save'
	};
	var myAjax = new Ajax.Updater('questionList',
			'FlagCriteriaActionAjax.action', {
				method :'post',
				parameters :pars,
				onComplete : function() {
					stopThinking();
					closeNewCriteria();
					refreshList();
				}
			});
}
