<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Notes</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style>
div.auditHeader fieldset {
	width: 40%;
}
</style>
</head>
<body>
<h1><s:property value="account.name"/></h1>
<div class="buttons" style="float: right">
	<a class="picsbutton" onclick="window.close();" href="#">Close</a>
	<s:if test="permissions.admin || permissions.userId == note.createdBy.id">
		<pics:permission perm="EditNotes" type="Edit">
			<a class="picsbutton" href="?id=<s:property value="id"/>&note.id=<s:property value="note.id"/>&mode=edit&embedded=<s:property value="embedded"/>">Edit</a>
		</pics:permission>
	</s:if>
</div>

<div id="auditHeader" class="auditHeader">
<fieldset>
<ul>
	<li><label>Category:</label>
		<s:property value="note.noteCategory"/>
	</li>
	<s:if test="note.employee != null">
		<li><label>Employee:</label>
			<s:property value="note.employee.displayName"/>
		</li>
	</s:if>
	<li><label>Priority:</label>
		<s:property value="note.priority"/>
	</li>
	<li><label>Note Status:</label>
		<s:property value="note.status"/>
	</li>
</ul>
</fieldset>
<fieldset>
<ul>
	<s:if test="note.canContractorView">
		<li><label><s:property value="account.type"/>:</label>Can View</li>
	</s:if>
	<li><label>Viewable By:</label>
		<s:property value="note.viewableBy.name"/>
		<s:if test="note.viewableBy.id == 1 && !note.canContractorView">
			except <s:property value="account.type"/>
		</s:if>
	</li>
	<li><label>Created By:</label>
		<s:property value="note.createdBy.name"/> - <s:property value="note.createdBy.account.name"/>
	</li>
	<li><label>Created:</label>
		<s:date name="note.creationDate"/>
	</li>
</ul>
</fieldset>
<div class="clear"></div>
</div>

<div><s:property value="note.summary"/></div>

<div><s:property value="note.bodyHtml" escape="false"/></div>

<s:if test="note.attachment != null">
	<div><a href="NoteEditor.action?button=attachment&note.id=<s:property value="note.id"/>" target="_BLANK">Click here to view attachment.</a></div>
</s:if>

<div><button name="button" class="picsbutton" type="button" value="cancel" onclick="window.close();">Close</button></div>
</body>
</html>
