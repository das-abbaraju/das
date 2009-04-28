
function showCriteria(opID , questionID) {
	startThinking({div:'criteriaEdit', type: 'large', message: "getting Flag Criteria from server.."});
	$('criteriaEdit').show();
	
	var pars = { 'id': opID, 'question.id':questionID };
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars
	});
}

function closeEditCriteria() {
	$('criteriaEdit').innerHTML = '';
}

function addNewCriteria(opID, questionID) {
	var pars = { 'id': opID, 'question.id': questionID };
	
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() { }
	});
}

function saveCriteria(opID, questionID) {
	alert($('criteriaEditForm').serialize());
}
