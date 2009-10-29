/** function with searching, sorting, and page changing **/
function download(url) {
	newurl = url + "CSV.action?" + $('#form1').serialize();
	popupWin = window.open(newurl, url, '');
}

function changePage( formid, pageNum ) {
	var search = $("#"+formid);
	search.find('[name=showPage]').val(pageNum);
	runSearch(search);
}

function changeOrderBy( formid, orderBy ) {
	var search = $("#"+formid);
	search.find('[name=showPage]').val("1");
	search.find('[name=orderBy]').val(orderBy);
	runSearch(search);
}

function changeStartsWith( formid, v ) {
	var search = $("#"+formid);
	search.find('[name=showPage]').val("1");
	search.find('[name=filter.startsWith]').val(v);
	runSearch(search);
}

function clickSearch( formid ) {
	var search = $("#"+formid);
	search.find('[name=showPage]').val("1");
	search.find('[name=filter.startsWith]').val("");
	runSearch(search);
	if (search.find('[name=filter.allowMailMerge]').val() == "true")
		$('#write_email_button').show();
	return false;
}

function clickSearchSubmit( formid ) {
	var search = $("#"+formid);
	search.find('[name=showPage]').val("1");
	search.find('[name=filter.startsWith]').val("");
}

function runSearch(search) {
	var ajax = $(search).find('[name=filter.ajax]').val();
	if (ajax == "false") {
		$(search).submit();
	} else {
		// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
		startThinking({div:'report_data', type: 'large', message: 'finding search results'});
		var destinationAction = $(search).find('[name=filter.destinationAction]').val();
		var data = $(search).toObj();
		$('#report_data').load(destinationAction+'Ajax.action', data, function() {wireClueTips()});
	}
}

function toggleBox(name) {
	var box = $('#'+name+'_select');
	var result = $('#'+name+'_query');
	result.hide();
	box.toggle();
	if (box.is(':visible'))
		return;

	updateQuery(name);
	result.show();
}

function showTextBox(name) {
	var textBox = $('#'+name);
	var result = $('#'+name+'_query'); 
	result.hide();
	textBox.toggle();
	if(textBox.is(':visible'))
		return;

	textQuery(name);
	result.show();	
}

function clearSelected(name) {
	$("#"+name).find('option').attr({'selected': false});
	updateQuery(name);
}

function updateQuery(name) {
	var box = $("#"+name);
	var result = $("#"+name+'_query');
	var queryText = '';
	box.find('option:selected').each(function(i,e){
		if (queryText != '') queryText += ", ";
		queryText += $(e).text();
	});
	
	if (queryText == '') {
		queryText = 'ALL';
	}
	result.text(queryText);
}

function textQuery(name) {
	var startField = $("#"+name+'1');
	var endField = $("#"+name+'2');
	var result = $("#"+name+'_query');
	var queryText = '';
	if(startField.val() != '' && endField.val() != '')
		queryText = 'between '+startField.val()+' and '+ endField.val();
	if(startField.val() != '' && endField.val() == '')
		queryText = 'after '+ startField.val();
	if(startField.val() == '' && endField.val() != '')
		queryText = 'before '+ endField.val();
	
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

function wireClueTips() {
	$("a.contractorQuick").cluetip({
		sticky: true, 
		hoverClass: 'cluetip', 
		clickThrough: true, 
		ajaxCache: true,
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		hoverIntent: {interval: 200},
		arrows: true,
		dropShadow: false,
		width: 400,
		cluetipClass: 'jtip',
		ajaxProcess:      function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
}