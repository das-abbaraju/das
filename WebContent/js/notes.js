function filter(returnType, filter, value) {
	if (accountID == null) {
		alert("accountID is not set");
		return;
	}
	var divName = returnType + 'List';
	var data = {
			id: accountID,
			button: 'refresh',
			returnType: returnType,
			filter: value
	};
	
	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	$('#'+divName).load(accountType+'NotesAjax.action', data);
}


function refresh(returnType) {
	if (accountID == null) {
		alert("accountID is not set");
		return;
	}

	var divName = returnType + 'List';
	var data = {
			id: accountID,
			button: 'refresh',
			returnType: returnType
	};

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	$('#'+divName).load(accountType+'NotesAjax.action', data);
}

function refreshNoteCategory(accountID, defaultCategory) {
	var divName = 'notesList';
	startThinking({div: 'thinking_' + divName, message: "Refreshing Notes", type: "large"});

	var data = {id: accountID, noteCategory: defaultCategory};
	$('#'+divName).load('EmbeddedNotesAjax.action', data);
}

function runNoteSearch(returnType) {
	var divName = returnType + 'List';

	var data = $('#'+returnType+'Form').toObj();
	
	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	$('#'+divName).load(accountType+'NotesAjax.action', data);
}

function updateNotePage(accountID,button,returnType,result) {
	var divName = returnType + 'List';

	var data = {
			id: accountID,
			button: button,
			returnType: returnType,
			'filter.firstResult': result
	};

	startThinking({div: 'thinking_' + divName, message: "Filtering List"});
	$('#'+divName).load(accountType+'NotesAjax.action', data);
}

function noteEditor(accountID, noteID, mode, defaultCategory) {
	var day = new Date();
	var id = day.getTime();
	var url = 'NoteEditor.action?id=' + accountID + '&note.id=' + noteID + '&mode=' + mode;
	if (defaultCategory != null)
		url += '&note.noteCategory='+defaultCategory;
	else
		url += '&embedded=0';
	
	var windowOptions = 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=770,height=550';
	
	var wnd = window.open(url, id, windowOptions);
	wnd.focus();

}
