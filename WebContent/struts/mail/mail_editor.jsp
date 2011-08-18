<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

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
<s:if test="!editTemplate">
	<br />
	Reply to:
	<s:radio name="fromMyAddress" list="fromAddresses" ></s:radio><br />
</s:if>
<s:textarea name="templateBody" rows="20" onkeyup="dirtyOn();" />
<pics:permission perm="DevelopmentEnvironment">
	<s:checkbox name="templateAllowsVelocity" id="templateAllowsVelocity" onclick="dirtyOn();" /><label for="templateAllowsVelocity">Template Allows Velocity Tags</label><br />
	<s:checkbox name="templateHtml" id="templateHtml" onclick="dirtyOn();" /><label for="templateHtml">Template is HTML</label>
</pics:permission>
