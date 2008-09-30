<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Mass Mailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script language="JavaScript">

var dirty = false;
function dirtyOn() {
	$('buttonSave').removeClassName('disabled');
	dirty = true;
}

function showTemplateList() {
	$('draftEdit').hide();
	Effect.Fade('menu_selector', {duration: 0.3});
	Effect.Appear('chooseEmail');
}

function showTemplateNew() {
	Effect.Appear('div_newEmail');
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
	$('templateBody').focus();
}

function editEmail() {
	$('buttonEdit').hide();
	$('buttonSave').show();
	$('buttonNew').show();
	Effect.Fade('previewEmail');
	Effect.Appear('draftEmail');
}

function previewEmail(item) {
	var id = item.value;

	if (id > 0) {
		$('buttonSave').hide();
		$('buttonNew').hide();
		$('buttonEdit').show();
		
		Effect.Fade('draftEmail');
		Effect.Appear('previewEmail');
		
		var subject = $('templateSubject').value;
		var body = $('templateBody').value;
		
		var pars = "button=MailPreviewAjax&ids[0]=" + id + "&templateSubject=" + subject + "&templateBody=" + body + "&type=<s:property value="type" />";
		
		$('previewEmail').innerHTML = '<img src="images/ajax_process.gif" />';
		var myAjax = new Ajax.Updater('previewEmail','MailPreviewAjax.action',
						 {method: 'post',parameters: pars});
	} else {
		alert("You must select record to preview");
	}
}

function chooseTemplate(id) {
	editEmail();
	$('buttonSave').addClassName('disabled');
	
	Effect.Fade('chooseEmail', {duration: 0.3});
	Effect.Appear('menu_selector');
	Effect.Appear('draftEdit', {duration: 2});
	
	var pars = "button=MailEditorAjax&templateID=" + id + "&type=<s:property value="type" />";

	$('draftEmail').innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('draftEmail','MailEditorAjax.action',
					 {method: 'post',parameters: pars});
}

function deleteTemplate(id) {
	var deleteMe = confirm('Are you sure you want to delete this email template?');
	if (!deleteMe)
		return;
	
	var pars = "button=delete&id=" + id;

	var myAjax = new Ajax.Updater('','EmailTemplateSave.action',
		{
			method: 'post',
			parameters: pars,
			onsuccess: Effect.Fade('li_template'+id)
		});
}

function saveTemplate() {
	var id = $('templateID').value;
	var subject = $('templateSubject').value;
	var body = $('templateBody').value;
	var name = $('templateName').value;
}

function addTemplate() {
	var id = $('templateID').value;
	var subject = $('templateSubject').value;
	var body = $('templateBody').value;
	var name = $('templateName').value;
}

</script>
<style type="text/css">
#templateBody {
	color: black;
	background-color: #FAFAFA;
	width: 100%;
}

#emailPreview {
	color: black;
	background-color: #EEE;
	width: 100%;
	padding: 10px;
}

#templateChooser {
	padding: 0px;
	list-style-type: none;
	margin: 0px;
}

#templateChooser li {
	padding: 10px;
	margin: 10px;
}

#templateChooser a {
	background-color: #EEE;
	padding: 10px;
	text-decoration: none;
	border: 1px solid #DDD;
}

#templateChooser a:hover {
	background-color: #FFF;
	padding: 10px;
	text-decoration: none;
	border: 1px solid #A84D10;
}

#menu_selector {
	margin-bottom: 10px;
	border-bottom: 2px dotted #A84D10;
	padding: 20px;
}

</style>
</head>
<body>

<h1>Mass Mailer</h1>
<s:include value="../actionMessages.jsp" />
<s:form
	id="form1" method="post">
	<s:hidden name="type" />
	<s:hidden name="button" value="send" />

<table style="width: 100%;">
<tr>
	<td style="width: 20%">
		<h3><s:property value="type" /></h3>
		<s:select id="contractors" cssClass="forms"
			name="ids" size="%{list.size() < 40 ? list.size() : 40}" multiple="true" list="list" listKey="get('id')"
			listValue="get('name')" ondblclick="previewEmail(this);" />
	</td>
	<td style="vertical-align: top; padding-left: 20px;">
		<div class="buttons" id="menu_selector" style="display: none;">
			<button id="buttonPick" type="button" onclick="showTemplateList();" title="Choose another email template">Pick Template</button>
			<button id="buttonPreview" type="button" onclick="previewEmail($('contractors'));" title="Preview the email with the selected contractor">Preview</button>
			<button id="buttonNew" type="button" onclick="showTemplateNew();" title="Save the email as a NEW template for future use">Save as New</button>
			<button id="buttonSave" type="button" onclick="showTemplateSave();" title="Save this email as a template for future use">Save Template</button>
			<button id="buttonEdit" style="display: none" type="button" onclick="editEmail();" title="Continue editing the email">Continue Editing</button>
			<button class="positive" type="button" onclick="sendEmails();">Send	Emails</button>
			<br clear="all">
		</div>
		<br clear="all" />
		<table id="chooseEmail" style="width: 100%; position: relative;">
			<tr>
				<td style="vertical-align: top;">
				<div
					style="color: #A84D10; padding: 20px; font-size: 18px; border: 1px solid #A84D10; text-align: center;">Select
				an Email <br />
				Template to Use</div>
				</td>
				<td valign="top">
				<ul id="templateChooser">
					<li><a href="javascript: chooseTemplate(0);">~ Start with
					a Blank Email ~</a></li>
					<s:iterator value="emailTemplates">
						<li id="li_template<s:property value="id"/>"><nobr><a
							href="javascript: chooseTemplate(<s:property value="id"/>);"><s:property
							value="templateName" /></a>
							<a
							href="javascript: deleteTemplate(<s:property value="id"/>);" title="Remove this template"><img src="images/cross.png" /></a></nobr></li>
					</s:iterator>
				</ul>
				</td>
			</tr>
		</table>
		<div id="draftEdit" style="display: none; position: relative;">
			<div id="draftEmail" style="position: static;"></div>
			<div id="previewEmail" style="display: none; position: static;"></div>
			<br clear="all" />
		</div>
	</td>
</tr>
</table>
</s:form>
</body>
</html>
