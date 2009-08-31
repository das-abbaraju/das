<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Notes</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript">
function closePage() {
	try {
<s:if test="embedded">
		opener.refreshNoteCategory('<s:property value="id"/>', '<s:property value="note.noteCategory"/>');
</s:if>
<s:else>
		opener.refresh('notes');
</s:else>
		opener.focus();
	} catch(err) {}
	self.close();
}
</script>
</head>
<body>
<s:include value="../actionMessages.jsp"></s:include>

<s:form id="editNotes" enctype="multipart/form-data" method="POST">
<s:hidden name="id"/>
<s:hidden name="note.id"/>
<s:hidden name="embedded"/>
<fieldset class="form">
	<div>
		<input class="picsbutton positive" name="button" type="submit" value="Save" />
		<s:if test="note.id > 0">
			<pics:permission perm="EditNotes" type="Delete">
				<input class="picsbutton negative" name="button" type="submit" value="Hide" />
			</pics:permission>
		</s:if>
		<input class="picsbutton" name="button" type="button" value="Close" onclick="closePage();" />
	</div>
<ol>
	<li><label>Note:</label>
		<s:textfield name="note.summary" maxlength="150" size="60" />
	</li>
	<li><label>Category:</label>
		<span style=""><s:radio theme="pics" list="filter.categoryList" name="note.noteCategory" /></span>
	</li>
	<li><label>Priority:</label>
		<s:radio theme="pics" list="filter.priorityList" name="note.priority" />
	</li>
	<li><label>Status:</label>
		<s:radio theme="pics" list="filter.statusList" name="note.status" />
	</li>
	<li><label>Can <s:property value="account.type"/> View:</label>
		<s:checkbox name="note.canContractorView"></s:checkbox>
	</li>
	<li><label>Viewable By:</label>
		<s:radio theme="pics" list="viewableByList" name="viewableBy"></s:radio>
		<s:select list="facilities" listKey="id" listValue="name" name="viewableByOther"></s:select>			
	</li>
	<li id="liAdditionalText"><label>Additional Text (optional):</label>
		<s:textarea name="note.body" cols="50" rows="6"></s:textarea>
	</li>
	<li><label>File Attachment:</label>
		<s:file name="file"></s:file>
		<s:if test="note.attachment != null">
			<a href="NoteEditor.action?note.id=<s:property value="note.id"/>&button=attachment" target="_BLANK">Click here to view attachment.</a>
			<a href="NoteEditor.action?id=<s:property value="id"/>&note.id=<s:property value="note.id"/>&button=remove&embedded=<s:property value="embedded"/>" class="remove">Remove</a>
		</s:if>
	</li>
</ol>
</fieldset>
</s:form>
<br clear="all" /><br />

</body>
</html>
