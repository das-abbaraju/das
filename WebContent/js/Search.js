/** function with searching, sorting, and page changing **/
function changePage( formid, pageNum ) {
	document.getElementById( formid )['showPage'].value = pageNum;
	document.getElementById( formid ).submit();
	return false;
}

function changeOrderBy( formid, orderBy ) {
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid )['orderBy'].value = orderBy;
	document.getElementById( formid ).submit();
	return false;
}

function changeStartsWith( formid, v ) {
	document.getElementById( formid )['filter.startsWith'].value = v;
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid ).submit();
	return false;
}

function runSearch( formid ) {
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid )['filter.startsWith'].value = "";
	return true;
}

function runSearchAjax( formid, actionName ) {
	// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div 
	var search = $(formid);
	$search['showPage'].value = "1";
	$search['filter.startsWith'].value = "";
	
	$('report_data').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' /> finding search results";
	var pars = $(formid).serialize();
	var myAjax = new Ajax.Updater('report_data',actionName+'.action', 
	{
		method: 'post', 
		parameters: pars
	});
}

/*** FILTER STUFF ****/
var cal2 = new CalendarPopup('caldiv2');
cal2.offsetY = -110;
cal2.setCssPrefix("PICS");
cal2.showNavigationDropdowns();

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
	var box = $(name);
	for(i=0; i < box.length; i++)
		box.options[i].selected = false
	updateQuery(name);
}

function updateQuery(name) {
	var box = $(name);
	var result = $(name+'_query');
	var queryText = '';
	var values = $F(box);
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
	var startField = $(name+'1');
	var endField = $(name+'2');
	var result = $(name+'_query');
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
	result.update(queryText);
}

function clearTextField(name) {
	var box = $(name);
	var startField = $(name+'1');
	var endField = $(name+'2');
	startField.value = "";
	endField.value = ""; 
	textQuery(name);
}

function showSearch()
{
	$('showSearch').hide();
	$('hideSearch').show();
	Effect.SlideDown('form1',{duration:.3});
	return false;
}

function hideSearch()
{
	$('hideSearch').hide();
	$('showSearch').show();
	Effect.SlideUp('form1',{duration:.5});
	return false;
}
