var templateID = 0;
var dirty = false;
var type = "";

function dirtyOn() {
	$('buttonSave').removeClassName('disabled');
	dirty = true;
}

function showTemplateList() {
	refreshList();
	$('previewEmail').hide();
	$('draftEmail').hide();
	$('menu_selector').hide();
	$('chooseEmail').show();
}

function sendEmails() {
	// Select all of the contractors left in the box, then submit the form
	var contractors = $('contractors');
	for (var i = 0; i < contractors.length; i++)
		contractors.options[i].selected = true;
	
	$('form1').submit();
}

function addToken(tokens) {
	$('templateBody').value += " <" + tokens.value + ">";
	tokens.value = 0;
	dirtyOn();
	$('templateBody').focus();
}

function editEmail() {
	$('draftEdit').show();
	$('buttonEdit').hide();
	$('buttonSave').show();
	$('previewEmail').hide();
	Effect.Appear('draftEmail');
}

function previewEmail(item) {
	if (templateID == 0) {
		alert("Select an email template to use first");
		return;
	}
	var id = item.value;
	if (id > 0) {
		$('buttonSave').hide();
		$('buttonEdit').show();
		
		if ($('draftEmail').visible())
			Effect.SlideUp('draftEmail', {duration: 0.5});
		Effect.Appear('previewEmail');
		
		var pars = "button=MailPreviewAjax&previewID=" + id + "&type=" + type;
		pars += "&templateSubject=" + escape($('templateSubject').value);
		pars += "&templateBody=" + escape($('templateBody').value);
		
		$('previewEmail').innerHTML = '<img src="images/ajax_process2.gif" />';
		var myAjax = new Ajax.Updater('previewEmail','MailPreviewAjax.action',
						 {method: 'post',parameters: pars});
	} else {
		alert("You must select record to preview");
	}
}

function chooseTemplate(id) {
	$('messages').innerHTML = "";
	
	templateID = id;
	editEmail();
	
	$('buttonSave').addClassName('disabled');
	
	$('chooseEmail').hide();
	Effect.Appear('menu_selector');
	Effect.Appear('draftEdit', {duration: 1});
	
	var pars = "button=MailEditorAjax&templateID=" + id + "&type=" + type;

	$('draftEmail').innerHTML = '<img src="images/ajax_process2.gif" />';
	var myAjax = new Ajax.Updater('draftEmail','MailEditorAjax.action',
					 {method: 'post',parameters: pars});
}

function deleteTemplate(id) {
	var deleteMe = confirm('Are you sure you want to delete this email template?');
	if (!deleteMe)
		return;
	
	$('messages').innerHTML = "";
	
	var pars = "button=delete&id=" + id;

	var myAjax = new Ajax.Updater('messages','EmailTemplateSaveAjax.action',
		{
			method: 'post',
			parameters: pars,
			onSuccess: function(transport) {
				Effect.Fade('li_template'+id);
			}
		});
}

function addTemplate(id) {
	$('messages').innerHTML = "";
	
	var name = $('templateName').value;
	var pars = "button=save&id=" + id + "&template.listType=" + type + "&template.templateName=" + name;
	pars += "&template.subject=" + escape($('templateSubject').value);
	pars += "&template.body=" + escape($('templateBody').value);
	
	var myAjax = new Ajax.Updater('messages','EmailTemplateSaveAjax.action',
		{
			method: 'post',
			parameters: pars,
			onSuccess: function(transport) {
				Effect.Fade('div_saveEmail');
				$('buttonSave').addClassName('disabled');
				dirty = false;
				refreshList();
			}
		});
}

function removeSelected() {
	var item = $('contractors');
	var deleteMe = confirm('Are you sure you want to delete the selected items?');
	if (!deleteMe)
		return;
	
	for (i=item.length-1;i>=0;i--) {
   		if (item[i].selected) {
			 item.remove(i);
		}
	}
}

function refreshList() {
	var pars = "template.listType=" + type;
	var myAjax = new Ajax.Updater('templateChooser','EmailTemplateSaveAjax.action',
		{ method: 'post', parameters: pars});
	
}
