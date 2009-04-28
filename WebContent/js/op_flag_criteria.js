
function showCriteria(opID , questionID) {
	startThinking({div:'criteriaEdit', type: 'large', message: "getting Flag Criteria from server.."});
	$('criteriaEdit').show();
	new Effect.Opacity('criteriaList', { to: 0.5, duration: 0.7 });
	
	var pars = { 'id': opID, 'question.id':questionID };
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars
	});
}

function closeEditCriteria() {
	$('criteriaEdit').innerHTML = '';
	$('criteriaEdit').hide();
	new Effect.Opacity('criteriaList', { to: 1.0, duration: 0.3 });
}

function addNewCriteria(opID, questionID) {
	startThinking({div:'criteriaAdd', type: 'large', message: "getting Flag Criteria from server.."});
	var pars = { 'id': opID, 'question.id': questionID };
	
	var myAjax = new Ajax.Updater('criteriaAdd','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() { }
	});
}

function saveCriteria(opID, questionID) {
	startThinking({message: "reloading criteria list.."});
	$('criteriaList').show();
	
	var pars = { 'id': opID, 'question.id':questionID };
	var myAjax = new Ajax.Updater('criteriaList','OperatorFlagCriteriaAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() {
			stopThinking();
			closeEditCriteria();
		}
	});
	//alert($('criteriaEditForm').serialize());
}

function showNewCriteria() {
	$('criteriaAdd').show();
	Effect.DropOut('addButton');
}
