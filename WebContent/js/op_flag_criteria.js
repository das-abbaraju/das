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
	// TODO save criteriaEditForm
	var pars = "";
	//Form.getElements('criteriaEditForm', 'button').invoke('disable');
	Form.getElements('criteriaEditForm').each(function (e) 
			{
				if (!$F(e).blank() && e.type != 'button')
					pars += e.serialize() + "&";
			}.bind(pars));
	// TODO handle DAO save
	pars += 'button=save';
	alert(pars);
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
			method: 'post',
			parameters: pars,
			onComplete: function() {
					stopThinking();
					refreshList();
					closeCriteriaEdit();
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
