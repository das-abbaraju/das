// EmployeeID, Translation and JSON variables are set in manage_employees.jsp
function startup() {
	$('#employees').dataTable({
		aoColumns: [
	            {bVisible: false},
	            {sType: "html"},
	            {sType: "html"},
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
	
	$('#addExcel').click(function(e) {
		e.preventDefault();
		showExcelUpload();
	});
	
	$('a.loadEmployee').click(function() {
		$('#employees tr.highlight').removeClass('highlight');
		// Put highlight class on the clicked row
		$(this).parent().parent().addClass('highlight');
	});
	
	if (getEmployeeIDFromHash() > 0)
		employeeID = getEmployeeIDFromHash();

	if (employeeID > 0)
		loadEmployee(employeeID);
}

function setupEmployee() {
	$.mask.definitions['S'] = '[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
	$('input.date').mask('99/99/9999');
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
		buttonText : translation_chooseADate,
		constrainInput : true,
		showAnim : 'fadeIn'
	});
}

function loadEmployee(id) {
	$('#employeeForm').load('ManageEmployees!loadAjax.action', {
		'employee' : id
	}, function() {
		setupEmployee();
	});
	return false;
}

function addJobRole(id) {
	startThinking({
		div : 'thinking_roles',
		message : translation_ajaxLoad
	});
	$('#employee_role').load('ManageEmployees!addRoleAjax.action', {
		'employee' : employeeID,
		childID : id
	});
}

function addJobSite(selection) {
	var id = $(selection).val();
	var name = $(selection).find('option[value="' + id + '"]').text().trim()
			.split(":");

	startThinking({
		div : 'employee_site',
		message : translation_ajaxLoad
	});
	$('#employee_site').load('ManageEmployees!addSiteAjax.action', {
		'employee' : employeeID,
		'op.id' : id,
		'op.name' : name[0]
	}, function () {
		setupDatepicker();
	});
}

function removeJobRole(id) {
	var remove = confirm(translation_removeRole);

	if (remove) {
		startThinking({
			div : 'thinking_roles',
			message : translation_ajaxLoad
		})
		$('#employee_role').load('ManageEmployees!removeRoleAjax.action', {
			'employee' : employeeID,
			childID : id
		});
	}

	return false;
}

function removeJobSite(id) {
	var remove = confirm(translation_removeProject);

	if (remove) {
		startThinking({
			div : 'thinking_sites',
			message : translation_ajaxLoad
		});
		$('#employee_site').load('ManageEmployees!removeSiteAjax.action', {
			'employee' : employeeID,
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
		message : translation_ajaxLoad
	})
	$('#employee_site').load(
			'ManageEmployees!newSiteAjax.action?'
					+ $('#newJobSiteForm input').serialize(), {
				'employee' : employeeID
			});
}

function editAssignedSites(id) {
	startThinking({
		div : 'thinking_sites',
		message : translation_ajaxLoad
	})
	$('#employee_site').load(
			'ManageEmployees!editSiteAjax.action?'
					+ $('#siteForm_' + id).serialize(), {
				'employee' : employeeID,
				childID : id
			});

	$.unblockUI();
	return false;
}
function showUpload() {
	url = 'EmployeePhotoUploadAjax.action?employeeID=' + employeeID;
	title = translation_uploadPhoto;
	pars = 'scrollbars=yes,resizable=yes,width=900,height=700,toolbar=0,directories=0,menubar=0';
	photoUpload = window.open(url, title, pars);
	photoUpload.focus();
}

function getSite(id) {
	$('#siteEditBox').load('ManageEmployees!getSiteAjax.action', {
		'employee' : employeeID,
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