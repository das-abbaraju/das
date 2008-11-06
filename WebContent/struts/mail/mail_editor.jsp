<%@ taglib prefix="s" uri="/struts-tags"%>

<h3><s:property value="templateName"/></h3>
<div id="div_saveEmail" style="display: none; margin-bottom: 150px">
	<fieldset>
	<legend><span>Save Template Form</span></legend>
	<ol>
		<li><label>Template name:</label>
			<s:textfield name="templateName" id="templateName" size="30" cssClass="forms" maxlength="50" />
			<s:if test="templateID > 0">
				<br />
				<input type="button" onclick="addTemplate(<s:property value="templateID" />);" value="Save" class="forms" />
			</s:if>
			<input type="button" onclick="addTemplate(0);" value="Save As New" class="forms" />
		</li>
	</ol>
	</fieldset>
</div>

<s:hidden name="templateID" />
<s:select cssClass="forms" id="tokens" name="tokens" headerKey="0"
	headerValue="- Add Field to Email -" listKey="tokenName"
	listValue="tokenName" list="tokens" onchange="addToken(this);" cssStyle="float: right;" />
<label>Subject:</label> <s:textfield name="templateSubject" size="60" maxlength="150" cssClass="forms" onchange="dirtyOn();" />
&nbsp;&nbsp;&nbsp;
<br />
<s:textarea name="templateBody" rows="20" onkeyup="dirtyOn();" />
<s:if test="permissions.email.endsWith('picsauditing.com')">
	<br />
	<s:radio name="fromMyAddress" list="#{'false':'info@picsauditing.com','true':permissions.email}" value="true" ></s:radio><br />
	<label>Password for <s:property value="permissions.email" />:</label> <s:textfield name="password" value=""></s:textfield>
</s:if>

<s:if test="templateAllowsVelocity">
	<div id="info">This template allows velocity tags</div>
</s:if>

