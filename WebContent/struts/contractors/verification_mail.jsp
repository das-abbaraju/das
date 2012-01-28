<%@ taglib prefix="s" uri="/struts-tags"%>
<form action="VerifyView.action?id=${id}" method="POST">
	<div>
		<label>Subject:</label>
		<s:textfield id="subject" name="previewEmail.subject" size="100" /><br/>
		<s:textarea id="body" name="previewEmail.body" rows="15" cols="100"/>
	</div>
	<div>
		<button name=button class="picsbutton" onclick="previewEmail();">Cancel</button>
		<s:submit cssClass="picsbutton positive" method="sendEmail" value="Send Email" />
		<a onClick="window.open('NoteEditor.action?id=<s:property value="id"/>&note=0&mode=edit&embedded=0&note.noteCategory=Audits&note.canContractorView=true','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=770,height=550'); return false;"
			href="#" title="opens in new window" class="picsbutton positive">Add Note</a>
	</div>
</form>
