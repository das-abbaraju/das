<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="div_saveEmail" style="display: none;">
	<label>Template Name:</label> <s:textfield name="templateName" id="templateName" size="30" cssClass="forms" maxlength="50" />
<s:if test="templateID > 0">
	<input type="button" onclick="addTemplate(<s:property value="templateID" />);" value="Save" class="forms" />
</s:if>
	<input type="button" onclick="addTemplate(0);" value="Save As New" class="forms" />
</div>

<s:hidden name="templateID" />
<s:select cssClass="forms" id="tokens" name="tokens" headerKey="0"
	headerValue="- Add Field to Email -" listKey="tokenName"
	listValue="tokenName" list="tokens" onchange="addToken(this);" cssStyle="float: right;" />
<label>Subject:</label> <s:textfield name="templateSubject" size="60" maxlength="150" cssClass="forms" onchange="dirtyOn();" />
&nbsp;&nbsp;&nbsp;
<br />
<s:textarea name="templateBody" rows="20" onkeyup="dirtyOn();" />
