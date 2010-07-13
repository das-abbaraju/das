<%@ taglib prefix="s" uri="/struts-tags"%>

<h3><s:property value="templateName"/></h3>
<div id="div_saveEmail" style="display: none;">
	<fieldset class="form bottom">
	<h2 class="formLegend">Save Template Form</h2>
	<ol>
		<li><label>Template name:</label>
			<s:textfield name="templateName" id="templateName" size="30" cssClass="forms" maxlength="50" />
			<s:if test="templateID > 0">
				<input type="button" onclick="addTemplate(<s:property value="templateID" />);" value="Save" class="picsbutton positive" />
			</s:if>
			<input type="button" onclick="addTemplate(0);" value="Save As New" class="picsbutton positive" />
		</li>
	</ol>
	</fieldset>
</div>
<s:if test="type.toString() == 'Contractor'">
	Send to <s:select
		cssClass="forms" name="recipient" value="%{recipient.description}"
		listKey="description" listValue="description" onchange="dirtyOn();"
		list="@com.picsauditing.actions.users.ManageUserPermissions@permissionTypes" /> Users
	<br />
</s:if>
<s:hidden name="templateID" />
<s:select cssClass="forms" id="tokens" name="tokens" headerKey="0"
	headerValue="- Add Field to Email -" listKey="tokenName"
	listValue="tokenName" list="picsTags" onchange="addToken(this);" cssStyle="float: right;" />
<label>Subject:</label> <s:textfield name="templateSubject" size="60" maxlength="150" cssClass="forms" onchange="dirtyOn();" />
&nbsp;&nbsp;&nbsp;
<br />
<s:textarea name="templateBody" rows="20" onkeyup="dirtyOn();" />
	<br />
	Reply to:
	<s:radio name="fromMyAddress" list="#{'false':'info@picsauditing.com','true':permissions.email}" value="%{fromMyAddress}" ></s:radio><br />
<s:if test="templateAllowsVelocity">
	<div class="info">This template allows velocity tags</div>
</s:if>

