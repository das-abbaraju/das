<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:set name="workflowID" value="%{id}" />
<s:if test="steps.size() > 0">
<h3><s:property value="workFlow.name"/></h3>
<table class="report" id="workflowStepsTable">
	<thead>
		<tr>
			<th>Old Status</th>
			<th>New Status</th>
			<th>Email Template</th>
			<th>Note Required</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="steps" >
			<tr id="<s:property value="id" />">
				<td><s:select list="@com.picsauditing.jpa.entities.AuditStatus@getValuesWithDefault()" name="oldStatus" /></td>
				<td><s:select list="@com.picsauditing.jpa.entities.AuditStatus@values()" listValue="name()" listKey="name()" name="newStatus" /></td>
				<td>
					<s:select list="emailTemplates" listValue="templateName" name="emailTemplateID" value="emailTemplate.id" listKey="id" headerKey="0" headerValue="- Select A Template -" />
					<s:if test="emailTemplate.id > 0">
						<a href="EditEmailTemplate.action?templateID=<s:property value="emailTemplate.id" />&type=<s:property value="emailTemplate.listType"/>" class="go" >Go</a>
					</s:if>
				</td>
				<td class="center"><s:checkbox name="noteRequired" /></td>
				<td><a href="#" onclick="editStep(<s:property value="id" />, <s:property value="#workflowID" />); return false;" class="save"></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>

<pics:permission perm="ManageAuditWorkFlow" type="Edit">
	<a href="#" onclick="$('#form_steps').show();return false;" class="add">Add Workflow Step</a>
	<s:form id="form_steps" cssStyle="display: none">
		<fieldset class="form">
		<s:hidden name="id" value="%{id}" />
		<s:hidden name="button" value="Add" />
			<h2 class="formLegend">Add Workflow Step</h2>
			<ol>
				<li><label>Old Status:</label>
					<s:select list="@com.picsauditing.jpa.entities.AuditStatus@getValuesWithDefault()" name="oldStatus" /></li>
				<li><label>New Status:</label>
					<s:select list="@com.picsauditing.jpa.entities.AuditStatus@values()" listKey="name()" listValue="name()" name="newStatus" /></li>
				<li><label>Email Template:</label>
					<s:select list="emailTemplates" listValue="templateName" name="emailTemplateID" listKey="id" headerKey="0" headerValue="- Select A Template -" />
				</li>
				<li><label>Note Required</label>
					<s:checkbox name="noteRequired" /></li>
			</ol>
		</fieldset>
		<br clear="all" />
		<fieldset class="forms submit">
			<input type="button" value="Add" name="button" class="picsbutton positive" onclick="addStep(); return false;" />
			<input type="button" value="Cancel" onclick="$('#form_steps').hide(); return false;" class="picsbutton negative" />
		</fieldset>
	</s:form>
</pics:permission>