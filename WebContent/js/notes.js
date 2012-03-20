function filter(returnType, filter, value) {
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
	data[filter] = value;
	
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

	$('#'+divName).load(accountType+'NotesAjax.action', data);
}

function refreshNoteCategory(accountID, defaultCategory) {
	var divName = 'notesList';

	var data = {id: accountID, noteCategory: defaultCategory};
	$('#'+divName).load('EmbeddedNotesAjax.action', data);
}

function runNoteSearch(returnType) {
	var divName = returnType + 'List';

	var data = $('#'+returnType+'Form').serialize();
	
	$.post(accountType+'NotesAjax.action', data, function(text, status){
		if (status=='success')
			$('#'+divName).html(text);
	});
}

function updateNotePage(accountID,button,returnType,result) {
	var divName = returnType + 'List';

	var data = {
			id: accountID,
			button: button,
			returnType: returnType,
			'filter.firstResult': result,
			'filter.category': $('#notesForm_filter_category').val()
	};

	$('#'+divName).load(accountType+'NotesAjax.action', data);
}

function noteEditor(accountID, noteID, mode, defaultCategory) {
	var day = new Date();
	var id = day.getTime();
	var url = 'NoteEditor.action?id=' + accountID + '&note=' + noteID + '&mode=' + mode;
	if (defaultCategory != null)
		url += '&note.noteCategory='+defaultCategory;
	else
		url += '&embedded=0';
	
	var windowOptions = 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=770,height=550';
	
	var wnd = window.open(url, id, windowOptions);
	wnd.focus();

}
