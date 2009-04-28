
function showCriteria(opID , questionID) {
	var pars = { 'id': opID, 'question.id':questionID };
	var myAjax = new Ajax.Updater('criteriaEdit','FlagCriteriaActionAjax.action', {
		method : 'post',
		parameters : pars,
		onComplete: function() { }
	});
}
