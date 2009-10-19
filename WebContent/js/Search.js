/** function with searching, sorting, and page changing **/
function download(url) {
	newurl = url + "CSV.action?" + $('#form1').serialize();
	popupWin = window.open(newurl, url, '');
}

function changePage( formid, pageNum ) {
	var search = $("#"+formid);
	search['showPage'].value = pageNum;
	runSearch(search);
}

function changeOrderBy( formid, orderBy ) {
	var search = $("#"+formid);
	search['showPage'].value = "1";
	search['orderBy'].value = orderBy;
	runSearch(search);
}

function changeStartsWith( formid, v ) {
	var search = $("#"+formid);
	search['showPage'].value = "1";
	search['filter.startsWith'].value = v;
	runSearch(search);
}

function clickSearch( formid ) {
	var search = $("#"+formid);
	search['showPage'].value = "1";
	search['filter.startsWith'].value = "";
	runSearch(search);
	if (search['filter.allowMailMerge'].value == "true")
		$('write_email_button').show();
	return false;
}

function clickSearchSubmit( formid ) {
	var search = $("#"+formid);
	search['showPage'].value = "1";
	search['filter.startsWith'].value = "";
}

function runSearch(search) {
	var ajax = search['filter.ajax'].value;
	if (ajax == "false") {
		search.submit();
	} else {
		// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
		$('report_data').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' /> finding search results";
		//Effect.Opacity('report_data', { from: 1.0, to: 0.7, duration: 0.5 });
		//alert('done');
		
		var destinationAction = search['filter.destinationAction'].value;
		var pars = search.serialize();
		var myAjax = new Ajax.Updater('report_data',destinationAction+'Ajax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				$('search').scrollTo();
			}
		});
	}
}

function toggleBox(name) {
	var box = $(name+'_select');
	var result = $(name+'_query');
	result.hide();
	box.toggle();
	if (box.visible())
		return;

	updateQuery(name);
	result.show();
}

function showTextBox(name) {
	var textBox = $(name);
	var result = $(name+'_query'); 
	result.hide();
	textBox.toggle();
	if(textBox.visible())
		return;

	textQuery(name);
	result.show();	
}

function clearSelected(name) {
	var box = $("#"+name);
	for(i=0; i < box.length; i++)
		box.options[i].selected = false
	updateQuery(name);
}

function updateQuery(name) {
	var box = $("#"+name);
	var result = $("#"+name+'_query');
	var queryText = '';
	var values = box.val();
	for(i=0; i < box.length; i++) {
		if (box.options[i].selected) {
			if (queryText != '') queryText = queryText + ", ";
			queryText = queryText + box.options[i].text;
		}
	}
	
	if (queryText == '') {
		queryText = 'ALL';
	}
	result.update(queryText);
}

function textQuery(name) {
	var startField = $("#"+name+'1');
	var endField = $("#"+name+'2');
	var result = $("#"+name+'_query');
	var queryText = '';
	if(startField.value != '' && endField.value != '')
		queryText = 'between '+startField.value+' and '+ endField.value;
	if(startField.value != '' && endField.value == '')
		queryText = 'after '+ startField.value;
	if(startField.value == '' && endField.value != '')
		queryText = 'before '+ endField.value;
	
	if (queryText == '') {
		queryText = '= ALL';
	}
	result.html(queryText);
}

function clearTextField(name) {
	var box = $("#"+name);
	var startField = $("#"+name+'1');
	var endField = $("#"+name+'2');
	startField.value = "";
	endField.value = ""; 
	textQuery(name);
}

function showSearch()
{
	$('#showSearch').hide();
	$('#hideSearch').show();
	$("#form1").slideDown();
	return false;
}

function hideSearch()
{
	$('#hideSearch').hide();
	$('#showSearch').show();
	$("#form1").slideUp();
	return false;
}
