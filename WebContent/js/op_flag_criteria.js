
function showCriteria(opID , questionID) {
	var pars = { 'id': opID, 'question.id':questionID };
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() { }
	});
}

function addNewCriteria(questionID) {
	var pars = { 'id': <s:property value="id"/>, 'question.id':questionID };
	
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() { }
	});
}
