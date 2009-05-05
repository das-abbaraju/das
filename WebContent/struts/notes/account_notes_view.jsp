<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Notes</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<style>
#auditHeader fieldset {
	width: 40%;
}
</style>
</head>
<body>
<h1><s:property value="contractor.name"/></h1>
<div id="auditHeader">
<div class="buttons" style="float: right">
	<button name="button" type="button" value="cancel" onclick="window.close();">Close</button>
	<s:if test="permissions.admin || permissions.userId == note.createdBy.id">
		<pics:permission perm="EditNotes" type="Edit">
			<a href="?id=<s:property value="id"/>&note.id=<s:property value="note.id"/>&mode=edit&embedded=<s:property value="embedded"/>">Edit</a>
		</pics:permission>
	</s:if>
</div>
<fieldset>
<ul>
	<li><label>Category:</label>
		<s:property value="note.noteCategory"/>
	</li>
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
		<li><label>Contractor:</label>Can View</li>
	</s:if>
	<li><label>Viewable By:</label>
		<s:property value="note.viewableBy.name"/>
		<s:if test="note.viewableBy.id == 1 && !note.canContractorView">
			except contractor
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

<p><s:property value="note.summary"/></p>

<div><s:property value="note.bodyHtml" escape="false"/></div>

<div class="buttons">
	<button name="button" type="button" value="cancel" onclick="window.close();">Close</button>
</div>
<br />
</body>
</html>
