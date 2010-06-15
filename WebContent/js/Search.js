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

// Checks if the state and country are selected. If so,
// ignore and clear out the country.
function checkStateAndCountry( formidState, formidCountry ) {
	if($('#'+formidState).find('option:selected').val() != null)
		clearSelected(formidCountry);
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
		var accountType = "";
		
		if ($(search).find('[name=filter.accountType]').val() != null)
			var accountType = "&accountType="+$(search).find('[name=filter.accountType]').val();
		
		var data = $(search).serialize();
		$.post(destinationAction+'Ajax.action?button=Search'+accountType, data, function(text, status) {
			$('#report_data').html(text);
			wireClueTips();
		});
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

function showInsuranceTextBoxes(name) {
	var textBox = $('#'+name);
	var result = $('#'+name+'_query'); 
	result.hide();
	textBox.toggle();
	if(textBox.is(':visible'))
		return;

	insuranceLimitsTextQuery(name);
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
	
	if (startField.val() != undefined) {
		if(startField.val() != '' && endField.val() != '')
			queryText = 'between '+startField.val()+' and '+ endField.val();
		if(startField.val() != '' && endField.val() == '')
			queryText = 'after '+ startField.val();
		if(startField.val() == '' && endField.val() != '')
			queryText = 'before '+ endField.val();
	} else if (endField.val() != '') {
		queryText = 'before '+ endField.val();
	}
	
	if (queryText == '') {
		queryText = '= ALL';
	}
	result.html(queryText);
}

function insuranceLimitsTextQuery(name) {
	var field1 = $("#"+name+'1');
	var field2 = $("#"+name+'2');
	var field3 = $("#"+name+'3');
	var field4 = $("#"+name+'4');
	var field5 = $("#"+name+'5');
	var result = $("#"+name+'_query');
	var queryText = '';
		
	if (field1.val() !== undefined && field1.val() != '' && field1.val() != '- Enter Amount -'){
		queryText += 'GL-EO: $' + field1.val(); 
	}
	if (field2.val() !== undefined && field2.val() != '' && field2.val() != '- Enter Amount -'){
		if(queryText != '')
			queryText += ', ';
		queryText += 'GL-GA: $' + field2.val(); 
	}
	if (field3.val() !== undefined && field3.val() != '' && field3.val() != '- Enter Amount -'){
		if(queryText != '')
			queryText += ', ';
		queryText += 'AL-CS: $' + field3.val(); 
	}
	if (field4.val() !== undefined && field4.val() != '' && field4.val() != '- Enter Amount -'){
		if(queryText != '')
			queryText += ', ';
		queryText += 'WC-EA: $' + field4.val(); 
	}
	if (field5.val() !== undefined && field5.val() != '' && field5.val() != '- Enter Amount -'){
		if(queryText != '')
			queryText += ', ';
		queryText += 'EX-EO: $' + field5.val(); 
	}
	
	if (queryText == '') {
		queryText = '= ALL';
	}
	result.html(queryText);
}

function clearTextField(name) {
	var box = $("#"+name);
	var startField = $("#"+name+'1').val('');
	var endField = $("#"+name+'2').val('');
	textQuery(name);
}

function clearInsuranceText(thefield)
{
	if(thefield.value == '- Enter Amount -')
		thefield.value = '';
}

function resetEmptyField(thefield)
{
	if(thefield.value == '' || thefield.value === undefined)
		thefield.value = '- Enter Amount -';
}

function clearInsuranceTextFields(name) {
	var box = $("#"+name);
	var field1 = $("#"+name+'1').val('- Enter Amount -');
	var field2 = $("#"+name+'2').val('- Enter Amount -');
	var field3 = $("#"+name+'3').val('- Enter Amount -');
	var field4 = $("#"+name+'4').val('- Enter Amount -');
	var field5 = $("#"+name+'5').val('- Enter Amount -');
	insuranceLimitsTextQuery(name);
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
	$('a.contractorQuick').add('a.operatorQuick').cluetip( {
		sticky : true,
		hoverClass : 'cluetip',
		mouseOutClose : true,
		clickThrough : true,
		ajaxCache : true,
		closeText : "<img src='images/cross.png' width='16' height='16'>",
		hoverIntent : {
			interval : 300
		},
		arrows : true,
		dropShadow : false,
		width : 400,
		cluetipClass : 'jtip',
		ajaxProcess : function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
}

function isNumber(field,position){
	var numberRegex = /^(\d{1,3}\,?)*$/;
	var value = $(field).val();
	
	if(value != undefined && value != '' && !numberRegex.test(value))
		$('#error'+position).text('* Please enter only numbers');
	else
		$('#error'+position).text('');
}