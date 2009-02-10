function filter(returnType, filter, value) {
	var divName = returnType + 'List';
	var pars = 'id='+conID+'&button=refresh&returnType='+returnType+'&'+filter+'='+value;

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}


function refresh(returnType) {
	if (conID == 0) return;

	var divName = returnType + 'List';
	var pars = 'id='+conID+'&button=refresh&returnType='+returnType;

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}

function runSearch(returnType) {
	var divName = returnType + 'List';

	var pars = $(returnType+'Form').serialize();
	
	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}
