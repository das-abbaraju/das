<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Mass Mailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script language="JavaScript">

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

function previewEmail(item) {
	var subject = $('templateSubject').value;
	var body = $('templateBody').value;
	
	var pars = "button=MailPreviewAjax&ids[0]=" + item.value + "&templateSubject=" + subject + "&templateBody=" + body + "&type=<s:property value="type" />";

	$('td_previewEmail').innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('td_previewEmail','MailPreviewAjax.action',
					 {method: 'post',parameters: pars});
}

function chooseTemplate(id) {
	$('chooseEmail').hide();
	
	var pars = "button=MailEditorAjax&templateID=" + id + "&type=<s:property value="type" />";

	$('draftEmail').innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('draftEmail','MailEditorAjax.action',
					 {method: 'post',parameters: pars});
}

</script>
<style type="text/css">
#templateBody {
	color: black;
	background-color: #FAFAFA;
	width: 95%;
	padding: 15px;
}

#emailPreview {
	color: black;
	background-color: #EEE;
	width: 95%;
	padding: 15px;
}

#templateChooser {
	padding: 0px;
	list-style-type: none;
	margin: 0px;
}

#templateChooser li {
	padding: 10px;
	margin: 3px;
	width: 200px;
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
</style>
</head>
<body>
<h1>Mass Mailer</h1>

<div>
<s:include value="../actionMessages.jsp" />
<s:form
	id="form1" method="post">
	<s:hidden name="button" value="send" />

	<table border="1" style="width: 100%;">
		<tr>
			<td rowspan="2"><s:property value="type" />: <br />
				<s:hidden name="type" />
				<s:select id="contractors" cssClass="forms"
					name="ids" size="%{list.size() < 40 ? list.size() : 40}" multiple="true" list="list" listKey="get('id')"
					listValue="get('name')" ondblclick="previewEmail(this);" />
			</td>
			<td style="vertical-align: middle;">
			<table id="chooseEmail">
				<tr>
					<td style="vertical-align: middle;">
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
							<li><a
								href="javascript: chooseTemplate(<s:property value="id"/>);"><s:property
								value="subject" /></a></li>
						</s:iterator>
					</ul>
					</td>
				</tr>
			</table>
			<div id="draftEmail"></div>
			</td>
		</tr>
		<tr>
			<td id="td_previewEmail" valign="top">
			<div id="info">Double-Click Contractor on Left to Preview Email</div>
			</td>
		</tr>
	</table>

	<div class="buttons right">
	<button class="positive" type="button" onclick="sendEmails();">Send
	Emails</button>
	</div>

</s:form></div>
</body>
</html>