<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<h3>
	<s:property value="templateName"/>
</h3>
<div id="div_saveEmail" style="display: none;">
	<fieldset class="form bottom">
		<h2 class="formLegend">
			<s:text name="EditEmailTemplate.SaveTemplateForm" />
		</h2>
		<ol>
			<li>
				<label>
					<s:text name="EmailTemplate.templateName" />:
				</label>
				<s:textfield name="templateName" id="templateName" size="30" cssClass="forms" maxlength="50" />
				
				<s:if test="templateID > 0">
					<input
						type="button"
						onclick="addTemplate(<s:property value="templateID" />);"
						value="<s:text name="button.Save" />"
						class="picsbutton positive"
					/>
				</s:if>
				<input
					type="button"
					onclick="addTemplate(0);"
					value="<s:text name="EditEmailTemplate.SaveAsNew" />"
					class="picsbutton positive"
				/>
			</li>
		</ol>
	</fieldset>
</div>

<s:if test="type.toString() == 'Contractor'">
	<s:text name="EditEmailTemplate.SendToUsers">
		<s:param>
			<s:select
				cssClass="forms dirtyOn"
				name="recipient"
				value="%{getText(getI18nKey('description'))}"
				listKey="description"
				listValue="description"
				list="@com.picsauditing.actions.users.ManageUserPermissions@permissionTypes"
			/>
		</s:param>
	</s:text>
	<br />
</s:if>

<s:hidden name="templateID" />
<s:hidden id="original_velocity" value="%{templateAllowsVelocity}" />
<s:hidden id="original_html" value="%{templateHtml}" />
<s:hidden id="original_translated" value="%{templateTranslated}" />

<s:select
	cssClass="forms"
	id="tokens"
	name="tokens"
	headerKey="0"
	headerValue="- %{getText('MassMailer.AddFieldToEmail')} -"
	listKey="name"
	listValue="name"
	list="picsTags"
	onchange="addToken(this);"
	ssStyle="float: right;"
/>

<label>
	<s:text name="EmailQueue.subject" />:
</label>
<s:textfield
	name="templateSubject"
	size="60"
	maxlength="150"
	cssClass="forms dirtyOn"
/>
&nbsp;&nbsp;&nbsp;

<s:if test="!editTemplate">
	<br />
	<s:text name="EditEmailTemplate.ReplyTo" />:
	<s:radio
		name="fromMyAddress"
		list="fromAddresses"
		theme="pics"
		cssClass="inline"
	/>
	<br />
</s:if>

<s:textarea name="templateBody" rows="20" cssClass="dirtyOn" />

<pics:permission perm="DevelopmentEnvironment">
	<s:checkbox name="templateAllowsVelocity" id="templateAllowsVelocity" cssClass="dirtyOn" />
	<label for="templateAllowsVelocity">Template Allows Velocity Tags</label>
	<br />
	<s:checkbox name="templateHtml" id="templateHtml" cssClass="dirtyOn" />
	<label for="templateHtml">Template is HTML</label>
	<br />
</pics:permission>

<s:if test="permissions.admin">
	<s:checkbox name="templateTranslated" id="templateTranslated" cssClass="dirtyOn" />
	<label for="templateTranslated">Template Has Translations</label> <br />
	<label>
		<s:text name="ManageAuditType.RequiredLanguages" />:
	</label>
	<s:optiontransferselect
		name="requiredLanguagesName"
		list="availableLocales"
		listKey="language"
		listValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
		doubleName="templateLanguages"
		doubleList="selectedLocales"
		doubleListKey="language"
		doubleListValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
		leftTitle="%{getText('ManageAuditType.AvailableLanguages')}"
		rightTitle="%{getText('ManageAuditType.SelectedLanguages')}"
		addToLeftLabel="%{getText('button.Remove')}"
		addToRightLabel="%{getText('button.Add')}"
		allowAddAllToLeft="false"
		allowAddAllToRight="false"
		allowSelectAll="false"
		allowUpDownOnLeft="false"
		allowUpDownOnRight="false"
		buttonCssClass="arrow"
		theme="pics"
	>
		<s:param name="sort" value="'false'" />
	</s:optiontransferselect>
</s:if>
