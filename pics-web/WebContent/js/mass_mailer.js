var templateID = 0;
var dirty = false;
var type = "";

$(function() {
	$('input[type=radio,checkbox].dirtyOn').live('click', function() {
		dirtyOn();
	});
	
	$('input[type=text].dirtyOn, textarea.dirtyOn').live('keyup', function() {
		dirtyOn();
	});
	
	$('select.dirtyOn').live('change', function() {
		dirtyOn();
	});
	
	$('.double-list-action input').live('click', function() {
		dirtyOn();
	});
});

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
	var r = confirm(translate("JS.MassMailer.SendingEmails", [ $('#sendSize').val() ]));
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
		alert(translate("JS.MassMailer.SelectTemplateFirst")); 
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
				templateAllowsVelocity: $('#templateAllowsVelocity').is(':checked'),
				templateHtml: $('#templateHtml').is(':checked'),
				recipient: $('#recipient').val()
		};
		$('#previewEmail').html('<img src="images/ajax_process2.gif" />');
		$('#previewEmail').load('MailPreviewAjax.action', data);
		
	} else {
		alert(translate("JS.MassMailer.SelectRecordToPreview"));
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
	var deleteMe = confirm(translate("JS.MassMailer.DeleteTemplate"));
	if (!deleteMe)
		return;
	
	$('#messages').html('');
	$('#messages').load('EmailTemplateSave!delete', { template: id }, 
		function(response, status) {
			if (status=='success')
				$('#li_template'+id).fadeOut();
		}
	);
}

function addTemplate(id) {
	$('#messages').html('');
	$('#templateLanguages option').attr('selected', true);
	
	var data = {
			template: id,
			'template.listType': type,
			'template.templateName': $('#templateName').val(),
			'template.recipient': $('#recipient').val(),
			'template.allowsVelocity': $('#templateAllowsVelocity').is(':checked'),
			'template.html': $('#templateHtml').is(':checked'),
			'template.translated': $('#templateTranslated').is(':checked'),
			'template.languages': $('#templateLanguages').val(),
			allowsVelocity: $('#original_velocity').val(),
			allowsHtml: $('#original_html').val(),
			allowsTranslations: $('#original_translated').val()
	};
	
	if ($('#templateTranslated').is(':checked')) {
		data['template.translatedSubject'] = $('#templateSubject').val();
		data['template.translatedBody'] = $('#templateBody').val();
	} else {
		data['template.subject'] = $('#templateSubject').val();
		data['template.body'] = $('#templateBody').val();
	}
	// Translated email template that will no longer be translated will overwrite the original email template.
	if ($('#original_translated').val() == "true" && $('#templateTranslated').is(':checked') == false) {
		if (confirm(translate("JS.MassMailer.ReplaceOriginalTemplate"))) {
			$('#messages').load('EmailTemplateSave!save.action', data,
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
	} else {
		$('#messages').load('EmailTemplateSave!save.action', data,
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
}

function removeSelected() {
	var deleteMe = confirm(translate("JS.MassMailer.DeleteItem"));
	if (!deleteMe)
		return;
	
	$('#contractors :selected').remove();
}

function refreshList() {
	$('#templateChooser').load('EmailTemplateSave', {'template.listType': type});
}
