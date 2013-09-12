/** function with searching, sorting, and page changing **/
function download(url) {
	var $form = $('#form1'),
		old_action = $form.attr('action'),
		new_action = url + "CSV.action?button=download&";

	$form.attr('action', new_action);
	$form.submit();
	$form.attr('action', old_action);
}

function changePage( formid, pageNum ) {
	var search = $("#"+formid);
	search.find('[name="showPage"]').val(pageNum);
	runSearch(search);
}

function changeOrderBy( formid, orderBy ) {
	var search = $("#"+formid);
	search.find('[name="showPage"]').val("1");
	search.find('[name="orderBy"]').val(orderBy);
	runSearch(search);
}

function changeStartsWith( formid, v ) {
	var search = $("#"+formid);
	search.find('[name="showPage"]').val("1");
	search.find('[name="filter.startsWith"]').val(v);
	runSearch(search);
}

// Checks if the country subdivision and country are selected. If so,
// ignore and clear out the country.
function checkCountrySubdivisionAndCountry( formidCountrySubdivision, formidCountry ) {
	if($('#'+formidCountrySubdivision).find('option:selected').val() != null)
		clearSelected(formidCountry);
}

function clickSearch( formid ) {
	var search = $("#"+formid);
	search.find('[name="showPage"]').val("1");
	search.find('[name="filter.startsWith"]').val("");
	runSearch(search);
	if (search.find('[name="filter.allowMailMerge"]').val() == "true")
		$('#write_email_button').show();
	if (search.find('[name="filter.allowMailReport"]').val() == "true")
		$('#send_report_button').show();
	return false;
}

function clickSearchSubmit( formid ) {
	var search = $("#"+formid);
	search.find('[name="showPage"]').val("1");
	search.find('[name="filter.startsWith"]').val("");
}

function runSearch(search) {
	var ajax = $(search).find('[name="filter.ajax"]').val();
	
	if (ajax == "false") {
		$(search).submit();
	} else {
		// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
		startThinking({div:'report_data', type: 'large', message: translate('JS.Filters.loading.FindingSearchResults')});
		var destinationAction = $(search).find('[name="filter.destinationAction"]').val();
		var accountType = "";
		
		if ($(search).find('[name="filter.accountType"]').val() != null)
			var accountType = "&accountType="+$(search).find('[name="filter.accountType"]').val();
		
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
		queryText = translate('JS.Filters.status.All');
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
			queryText = translate('JS.Filters.label.Between', new Array(startField.val(), endField.val()));
		if(startField.val() != '' && endField.val() == '')
			queryText = translate('JS.Filters.label.After', new Array(startField.val()));
		if(startField.val() == '' && endField.val() != '')
			queryText = translate('JS.Filters.label.Before', new Array(endField.val()));
	} else if (endField.val() != '') {
		queryText = translate('JS.Filters.label.Before', new Array(endField.val()));
	}
	
	if (queryText == '') {
		queryText = '= ' + translate('JS.Filters.status.All');
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
		
	if (field1.val() !== undefined && field1.val() != '' && field1.val() != translate('JS.Filters.label.EnterAmount')){
		queryText += 'GL-EO: $' + field1.val(); 
	}
	if (field2.val() !== undefined && field2.val() != '' && field2.val() != translate('JS.Filters.label.EnterAmount')){
		if(queryText != '')
			queryText += ', ';
		queryText += 'GL-GA: $' + field2.val(); 
	}
	if (field3.val() !== undefined && field3.val() != '' && field3.val() != translate('JS.Filters.label.EnterAmount')){
		if(queryText != '')
			queryText += ', ';
		queryText += 'AL-CS: $' + field3.val(); 
	}
	if (field4.val() !== undefined && field4.val() != '' && field4.val() != translate('JS.Filters.label.EnterAmount')){
		if(queryText != '')
			queryText += ', ';
		queryText += 'WC-EA: $' + field4.val(); 
	}
	if (field5.val() !== undefined && field5.val() != '' && field5.val() != translate('JS.Filters.label.EnterAmount')){
		if(queryText != '')
			queryText += ', ';
		queryText += 'EX-EO: $' + field5.val(); 
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
	if(thefield.value == translate('JS.Filters.label.EnterAmount'))
		thefield.value = '';
}

function resetEmptyField(thefield)
{
	if(thefield.value == '' || thefield.value === undefined)
		thefield.value = translate('JS.Filters.label.EnterAmount');
}

function clearInsuranceTextFields(name) {
	var box = $("#"+name);
	var field1 = $("#"+name+'1').val(translate('JS.Filters.label.EnterAmount'));
	var field2 = $("#"+name+'2').val(translate('JS.Filters.label.EnterAmount'));
	var field3 = $("#"+name+'3').val(translate('JS.Filters.label.EnterAmount'));
	var field4 = $("#"+name+'4').val(translate('JS.Filters.label.EnterAmount'));
	var field5 = $("#"+name+'5').val(translate('JS.Filters.label.EnterAmount'));
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
		width : 600,
		cluetipClass : 'jtip',
		ajaxProcess : function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
	
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
}

function isNumber(field,position){
	var numberRegex = /^(\d{1,3}\,?)*$/;
	var value = $(field).val();
	
	if(value != undefined && value != '' && !numberRegex.test(value))
		$('#error'+position).text(translate('JS.Filters.label.EnterOnlyNumbers'));
	else
		$('#error'+position).text('');
}