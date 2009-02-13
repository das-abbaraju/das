function filter(returnType, filter, value) {
	if (conID == null) {
		alert("conID is not set");
		return;
	}
	var divName = returnType + 'List';
	var pars = 'id='+conID+'&button=refresh&returnType='+returnType+'&'+filter+'='+value;
	
	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}


function refresh(returnType) {
	if (conID == null) {
		alert("conID is not set");
		return;
	}

	var divName = returnType + 'List';
	var pars = 'id='+conID+'&button=refresh&returnType='+returnType;

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}

function refreshNoteCategory(accountID, defaultCategory) {
	var divName = 'notesList';
	var pars = 'id='+accountID+'&noteCategory='+defaultCategory;

	startThinking({div: 'thinking_' + divName, message: "Refreshing Notes", type: "large"});
	var myAjax = new Ajax.Updater(divName, 'EmbeddedNotesAjax.action', {method: 'post', parameters: pars});
}

function runNoteSearch(returnType) {
	var divName = returnType + 'List';

	var pars = $(returnType+'Form').serialize();
	
	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	var myAjax = new Ajax.Updater(divName, 'ContractorNotesAjax.action', {method: 'post', parameters: pars});
}

function noteEditor(accountID, noteID, mode, defaultCategory) {
	var day = new Date();
	var id = day.getTime();
	var url = 'NoteEditor.action?id=' + accountID + '&note.id=' + noteID + '&mode=' + mode;
	if (defaultCategory != null)
		url += '&note.noteCategory='+defaultCategory;
	else
		url += '&embedded=0';
	
	var windowOptions = 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=700,height=500';
	
	var wnd = window.open(url, id, windowOptions);
	wnd.focus();

}
