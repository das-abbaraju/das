var templateID = 0;
var dirty = false;
var type = "";

function dirtyOn() {
	$('#buttonSave').removeAttr('disabled');
	dirty = true;
}

function showTemplateList() {
	refreshList();
	$('#previewEmail').hide();
	$('#draftEmail').hide();
	$('#menu_selector').hide();
	$('#chooseEmail').show();
}

function sendEmails() {	
	// Confirm box for sending out emails
	var r = confirm("You are sending " + $('#sendSize').val() 
			+ " emails. Please confirm if this is correct.");
	if(r == false) {
		return false;
	}
	
	$('#form1').submit();
}

function addToken(tokens) {
	$('#templateBody').val($('#templateBody').val()+"<"+tokens.value+">");
	tokens.value = 0;
	dirtyOn();
	$('#templateBody').focus();
}

function editEmail() {
	$('#draftEdit').show();
	$('#buttonEdit').hide();
	$('#buttonSave').show();
	if ($('#previewEmail').is(':visible'))
		$('#previewEmail').slideUp(500);
	$('#draftEmail').fadeIn();
}

function pEmail(id) { 
	if (templateID == 0) { 
		alert("Select an email template to use first"); 
		return;
	}
	
	if (id > 0) {
		$('#buttonSave').hide();
		$('#buttonEdit').show();
		
		if ($('#draftEmail').is(':visible'))
			$('#draftEmail').slideUp(500);
		$('#previewEmail').fadeIn();
		
		var data = {
				button:'MailPreviewAjax',
				previewID: id,
				type: type,
				templateSubject: $('#templateSubject').val(),
				templateBody: $('#templateBody').val(),
				recipient: $('#recipient').val()
		};
		$('#previewEmail').html('<img src="images/ajax_process2.gif" />');
		$('#previewEmail').load('MailPreviewAjax.action', data);
		
	} else {
		alert("You must select record to preview");
	}
}

function removeCon(id){
	$('#con_sel_list').load('MassMailerAjax.action', {removeID: id, button: 'removeCon'});
}

function chooseTemplate(id){
	chooseTemplate(id, false);
}

function chooseTemplate(id, edit) {
	$('#messages').html("");
	
	templateID = id;
	editEmail();
	
	$('#buttonSave').attr({'disabled':'disabled'});
	$('#chooseEmail').hide();
	
	$('#menu_selector').fadeIn();
	$('draftEdit').fadeIn(1000);
	
	var data = {
			button: 'MailEditorAjax',
			templateID: id,
			type: type,
			editTemplate: edit
	};

	$('#draftEmail').html('<img src="images/ajax_process2.gif" />');
	$('#draftEmail').load('MailEditorAjax.action', data);
}

function saveClick() {
	$('#div_saveEmail').fadeIn();
}

function deleteTemplate(id) {
	var deleteMe = confirm('Are you sure you want to delete this email template?');
	if (!deleteMe)
		return;
	
	$('#messages').html('');
	
	var data = {
			button: 'delete',
			id: id
	};

	$('#messages').load('EmailTemplateSaveAjax.action', data, 
			function(response, status) {
				if (status=='success')
					$('#li_template'+id).fadeOut();
			}
	);
}

function addTemplate(id) {
	$('#messages').html('');
	
	var data = {
			button: 'save',
			id: id,
			'template.listType': type,
			'template.templateName': $('#templateName').val(),
			'template.subject': $('#templateSubject').val(),
			'template.body': $('#templateBody').val(),
			'template.recipient': $('#recipient').val()
	};
	
	$('#messages').load('EmailTemplateSaveAjax.action', data,
			function(response, status) {
				if (status=='success') {
					$('#div_saveEmail').fadeOut();
					$('#buttonSave').attr({'disabled':'disabled'});
					dirty = false;
					refreshList();
				}
			}
	);
}

function removeSelected() {
	var deleteMe = confirm('Are you sure you want to delete the selected items?');
	if (!deleteMe)
		return;
	
	$('#contractors :selected').remove();
}

function refreshList() {
	$('#templateChooser').load('EmailTemplateSaveAjax.action', {'template.listType': type});
}
