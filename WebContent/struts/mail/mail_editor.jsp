<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="div_newEmail" style="display: none; text-align: center;">
	<label>Template Name:</label> <input type="text" name="templateName" id="templateName" value="" size="30" />
	<input type="button" onclick="addTemplate();" value="Add New" class="forms" disabled="disabled" />
</div>

<s:hidden name="templateID" />
<label>Subject:</label> <s:textfield name="templateSubject" size="60" cssClass="forms" onchange="dirtyOn();" />
&nbsp;&nbsp;&nbsp;
<s:select cssClass="forms" id="tokens" name="tokens" headerKey="0"
	headerValue="- Add Field to Email -" listKey="tokenName"
	listValue="tokenName" list="tokens" onchange="addToken(this);" />
<br />
<s:textarea name="templateBody" rows="20" onkeyup="dirtyOn();" />
