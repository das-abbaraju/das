<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Mass Emailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript" src="js/mass_mailer.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>

<script language="JavaScript">
type = "<s:property value="type" />";
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
<body
	<s:if test="templateID >= 0">onload="chooseTemplate(<s:property value="templateID"/>)"</s:if>
	<s:if test="emailTemplates.size == 0">onload="chooseTemplate(0)"</s:if>
>
<img src="images/beta.jpg" width="98" height="100" style="float: right;" title="This is a new feature. Please send us your feedback or suggestions." />

<h1>Mass Emailer</h1>
<s:include value="../actionMessages.jsp" />
<div id="messages"></div>
<s:form
	id="form1" method="post">
	<s:hidden name="type" />
	<s:hidden name="button" value="send" />

<table style="width: 100%;">
<tr>
	<td style="width: 20%">
		<h3><s:property value="type" /> (<s:property value="list.size()"/> entries)</h3>
		<s:select id="contractors" cssClass="forms"
			name="ids" size="%{list.size() < 40 ? list.size() : 40}" multiple="true" list="list" listKey="key"
			listValue="value" ondblclick="previewEmail(this);" title="Double click a row to preview email" />
			<div>* Double click preview</div>
			<button type="button" name="button" value="Remove" onclick="removeSelected()" title="select contractors to remove from list">Remove From List</button>
	</td>
	<td style="vertical-align: top; padding-left: 20px;">
		<div class="buttons" id="menu_selector" style="display: none;">
			<s:if test="emailTemplates.size > 0">
				<button id="buttonPick" type="button" onclick="showTemplateList();" title="Choose another email template">Pick Template</button>
			</s:if>
			<button id="buttonPreview" type="button" onclick="previewEmail($('contractors'));" title="Preview the email with the selected contractor">Preview</button>
			<pics:permission perm="EmailTemplates" type="Edit">
				<button id="buttonSave" type="button" onclick="Effect.Appear('div_saveEmail');" title="Save this email as a template for future use">Save...</button>
			</pics:permission>
			<button id="buttonEdit" style="display: none" type="button" onclick="editEmail();" title="Continue editing the email">Continue Editing</button>
			<button class="positive" type="button" onclick="sendEmails();">Send	Emails</button>
			<br clear="all">
		</div>
		<br clear="all" />
		<table id="chooseEmail" style="width: 100%; position: relative;">
			<tr>
				<td style="vertical-align: top;">
				<div
					style="color: #A84D10; padding: 20px; font-size: 18px; border: 1px solid #A84D10; text-align: center;">
					Select an Email <br />Template to Use
				</div>
				</td>
				<td valign="top">
				<ul id="templateChooser">
					<li><a href="javascript: chooseTemplate(0);"><nobr>~ Start with
					a Blank Email ~</nobr></a></li>
					<s:iterator value="emailTemplates">
						<li id="li_template<s:property value="id"/>"><nobr><a
							href="javascript: chooseTemplate(<s:property value="id"/>);"><s:property
							value="templateName" /></a>
							<pics:permission perm="EmailTemplates" type="Delete">
								<a
								href="javascript: deleteTemplate(<s:property value="id"/>);" title="Remove this template"><img src="images/cross.png" /></a>
							</pics:permission>
							</nobr></li>
					</s:iterator>
				</ul>
				</td>
			</tr>
		</table>
		<div id="draftEdit">
			<div id="draftEmail"></div>
			<div id="previewEmail" style="display: none;"></div>
		</div>
		<br clear="all" style="float: none;" />
	</td>
</tr>
</table>
</s:form>

</body>
</html>
