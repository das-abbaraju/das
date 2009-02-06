function filter(returnType, filter, value) {
	if (conID == 0) return;

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

function remove(returnType, noteID) {
	if (conID == 0) return;

	var divName = returnType + 'List';
	var pars = 'id='+conID+'&button=saveNote&returnType='+returnType+'&note.id='+noteID+'&note.status=Hidden';

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}

function showEditNotes(noteID) {
	if (conID == 0) return;
	
	var divName = 'noteEdit';
	
	$('noteEdit').show();

	var pars = 'id='+conID+'&button=noteEdit&returnType=edit&note.id='+noteID;

	startThinking({div: 'thinking_' + divName, message: "Loading the note for editing"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
	
}

