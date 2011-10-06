function startup() {
	$('#employees').dataTable({
		aoColumns: [
	            {bVisible: false},
	            {sType: "html"},
	            {sType: "html"},
	            null,
	            null,
	            null,
	            null
			],
		aaSorting: [[1, 'asc']],
		bJQueryUi: true,
		bStateSave: true,
		bLengthChange: false,
		oLanguage: {
			sSearch:"Search",
			sLengthMenu: '_MENU_', 
			sInfo:"_START_ to _END_ of _TOTAL_",
			sInfoEmpty:"",
			sInfoFiltered:"(filtered from _MAX_)" },
		fnRowCallback: function( nRow, aData, iDisplayIndex ) {
			if (aData[0] == employeeID || aData[0] == getEmployeeIDFromHash())
				$(nRow).not('.highlight').addClass('highlight');

			return nRow;
		}
	});

	$(window).bind('hashchange', function() {
		loadEmployee(getEmployeeIDFromHash());
	});

	$('a.loadEmployee').click(function() {
		$('#employees tr.highlight').removeClass('highlight');
		// Put highlight class on the clicked row
		$(this).parent().parent().addClass('highlight');
	});

	if (getEmployeeIDFromHash() > 0)
		employeeID = getEmployeeIDFromHash();

	if (employeeID > 0) {
		loadEmployee(employeeID);
	}

	$('#employeeFormDiv').delegate('#employee_nccer_link', 'click', function(e) {
		e.preventDefault();
		showNCCERUpload();
	});
	
	$('#employeeFormDiv').delegate('#deleteEmployee', 'click', function(e) {
		e.preventDefault();
		return confirm(translate('JS.ManageEmployees.confirm.DeleteEmployee'));
	});
	
	$('#content').delegate('#addExcel', 'click', function(e) {
		e.preventDefault();
		showExcelUpload();
	});
	
	$('#employeeFormDiv').delegate('.removeJobRole', 'click', function() {
		var id = $(this).attr('id').split('_')[1];
		return removeJobRole(id);
	});
	
	$('#employeeFormDiv').delegate('#employeeActive', 'click', function() {
		$('#termDate').toggle();
	});
	
	$('#employeeFormDiv').delegate('#employee_site .getSite', 'click', function(e) {
		e.preventDefault();
		var id = $(this).attr('id').split('_')[1];
		getSite(id);
	});
	
	$('#employeeFormDiv').delegate('#employee_site #hseOperator, #employee_site #oqOperator', 'change', function() {
		addJobSite($(this));
	});
	
	$('#employeeFormDiv').delegate('#employee_site .qualifiedTasks', 'click', function(e) {
		e.preventDefault();
		var statCount = $(this).attr('id').split('_')[1];
		
		$('#'+statCount+'_tasks').show();
	});
	
	$('#employeeFormDiv').delegate('#employee_site .closeSiteTasks', 'click', function(e) {
		e.preventDefault();
		var statCount = $(this).attr('id').split('_')[1];
		$('#'+statCount+'_tasks').hide();
	});
	
	$('#employeeFormDiv').delegate('#newJobSiteLink', 'click', function(e) {
		e.preventDefault();
		$('#newJobSiteForm').show();
		$(this).hide();
	});
	
	$('#employeeFormDiv').delegate('#newJobSiteForm .positive', 'click', function(e) {
		e.preventDefault();
		newJobSite();
	});
	
	$('#employeeFormDiv').delegate('#newJobSiteForm .cancelButton', 'click', function(e) {
		e.preventDefault();
		$('#newJobSiteForm').hide();
		$('#newJobSiteLink').show();
	});
	
	$('#siteEditBox').delegate('#saveSite', 'click', function(e) {
		e.preventDefault();
		var id = $(this).closest('form').attr('id').split('_')[1];
		editAssignedSites(id);
	});
	
	$('#siteEditBox').delegate('#removeSite', 'click', function() {
		var id = $(this).closest('form').attr('id').split('_')[1];
		return removeJobSite(id);
	});
	
	$('#siteEditBox').delegate('#closeEdit', 'click', function(e) {
		e.preventDefault();
		$.unblockUI();
	});
}

function setupEmployee() {
	$.mask.definitions['S'] = '[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
	$('input.date').mask('99/99/9999');
	
	json_previousTitles = $.map(json_previousTitles, function(value, i) {
		if (!!value) {
			return value;
		} else {
			return null;
		}
	});
	
	$('#locationSuggest').autocomplete(json_previousLocations);
	$('#titleSuggest').autocomplete(json_previousTitles);
	$('.cluetip').cluetip({
		closeText : "<img src='images/cross.png' width='16' height='16'>",
		arrows : true,
		cluetipClass : 'jtip',
		local : true,
		clickThrough : false
	});

	setupDatepicker();
}

function setupDatepicker() {
	$('.datepicker').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange : '1940:2039',
		showOn : 'button',
		buttonImage : 'images/icon_calendar.gif',
		buttonImageOnly : true,
		buttonText : translate('JS.ChooseADate'),
		constrainInput : true,
		showAnim : 'fadeIn'
	});
}

function loadEmployee(id) {
	startThinking({
		div: 'employeeFormDiv', 
		message: translate('JS.ManageEmployees.message.AjaxLoad')
	});
	
	$('#employeeFormDiv').load('ManageEmployees!loadAjax.action', { employee : id }, function() {
		setupEmployee();
	});
	
	return false;
}

function addJobRole(id) {
	startThinking({
		div : 'thinking_roles',
		message : translate('JS.ManageEmployees.message.AjaxLoad')
	});
	$('#employee_role').load('ManageEmployees!addRoleAjax.action', {
		employee : employeeID,
		childID : id
	});
}

function addJobSite(selection) {
	var id = $(selection).val();
	var name = $(selection).find('option[value="' + id + '"]').text().trim()
			.split(":");

	startThinking({
		div : 'employee_site',
		message : translate('JS.ManageEmployees.message.AjaxLoad')
	});
	$('#employee_site').load('ManageEmployees!addSiteAjax.action', {
		employee : employeeID,
		'op.id' : id,
		'op.name' : name[0]
	}, function() {
		setupDatepicker();
	});
}

function removeJobRole(id) {
	var remove = confirm(translate('JS.ManageEmployees.confirm.RemoveRole'));

	if (remove) {
		startThinking({
			div : 'thinking_roles',
			message : translate('JS.ManageEmployees.message.AjaxLoad')
		})
		$('#employee_role').load('ManageEmployees!removeRoleAjax.action', {
			employee : employeeID,
			childID : id
		});
	}

	return false;
}

function removeJobSite(id) {
	var remove = confirm(translate('JS.ManageEmployees.confirm.RemoveProject'));

	if (remove) {
		startThinking({
			div : 'thinking_sites',
			message : translate('JS.ManageEmployees.message.AjaxLoad')
		});
		$('#employee_site').load('ManageEmployees!removeSiteAjax.action', {
			employee : employeeID,
			childID : id
		}, function() {
			setupDatepicker();
		});
		$.unblockUI();
	}

	return false;
}

function newJobSite() {
	startThinking({
		div : 'thinking_sites',
		message : translate('JS.ManageEmployees.message.AjaxLoad')
	})
	$('#employee_site').load('ManageEmployees!newSiteAjax.action?' + $('#newJobSiteForm input').serialize(), {
		employee : employeeID
	});
}

function editAssignedSites(id) {
	startThinking({
		div : 'thinking_sites',
		message : translate('JS.ManageEmployees.message.AjaxLoad')
	})
	$('#employee_site').load(
			'ManageEmployees!editSiteAjax.action?'
					+ $('#siteForm_' + id).serialize(), {
				employee : employeeID,
				childID : id
			});

	$.unblockUI();
	return false;
}

function getSite(id) {
	$('#siteEditBox').load('ManageEmployees!getSiteAjax.action', {
		employee : employeeID,
		childID : id
	}, function() {
		setupDatepicker();
	});
	$.blockUI({
		message : $('#siteEditBox')
	});
}

function getEmployeeIDFromHash() {
	return parseInt(location.hash.substring(location.hash.indexOf("=") + 1));
}

function showNCCERUpload() {
	url = 'EmployeeNCCERUpload.action?employee=' + employeeID;
	title = translate('JS.ManageEmployees.message.UploadEmployees');
	pars = 'scrollbars=yes,resizable=yes,width=650,height=500,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url, title, pars);
	fileUpload.focus();
}