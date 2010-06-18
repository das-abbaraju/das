<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Mass Emailer</title>

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/mass_mailer.js?v=<s:property value="version"/>"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript">
type = "<s:property value="type" />";
<s:if test="templateID != 0">
$(function(){
	chooseTemplate(<s:property value="templateID"/>);
	$('a[rel*=facebox]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
});
</s:if>
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

<h1>Email Wizard <span class="sub">Step 4: Write Email</span></h1>
<div><a href="EmailWizard.action">&lt;&lt; Back to Steps 1-3 of the Email Wizard</a></div>
<s:include value="../actionMessages.jsp" />
<div id="messages"></div>
<s:form	id="form1" method="post">
	<s:hidden name="type" />
	<s:hidden name="button" value="send" />

<table style="width: 100%;">
<tr>
	<td style="width: 40%">
		<div id="con_sel_list">
			<s:include value="mass_con_email_list.jsp" />
		</div>
	</td>
	<td style="vertical-align: top; padding-left: 20px;">
		<div id="menu_selector" style="display: none;">
			<s:if test="emailTemplates.size > 0">
				<button id="buttonPick" class="picsbutton" type="button" onclick="showTemplateList();" title="Choose another email template">Pick Template</button>
			</s:if>
			<pics:permission perm="EmailTemplates" type="Edit">
				<button id="buttonSave" class="picsbutton" type="button" onclick="saveClick();" title="Save this email as a template for future use">Save...</button>
			</pics:permission>
			<button id="buttonEdit" class="picsbutton" style="display: none" type="button" onclick="editEmail();" title="Continue editing the email">Continue Editing</button>
			<s:if test="list.size() >0"><button class="picsbutton positive" type="button" onclick="sendEmails();">Send	Emails</button></s:if>
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
					<s:include value="select_templates.jsp"></s:include>
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
