<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ page import="com.picsauditing.service.i18n.TranslateUI" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div class="workflow_step_area">
<s:include value="../actionMessages.jsp"></s:include>

<h3><s:property value="workFlow.name"/></h3>
<s:hidden name="workflowID" value="%{workFlow.id}"/>
<h4>Workflow States</h4>
<s:if test="workFlow.states.size() > 0" >
	<table class="report" >
		<thead>
			<th>Status</th>
			<th>Name</th>
            <th>Contractor Can Edit</th>
            <th>Operator Can Edit</th>
            <th>Action</th>
            <th>Save</th>
			<th>Delete</th>
		</thead>
		<tbody>
			<s:iterator value="workFlow.states" var="state" >
				<tr id="state_<s:property value="#state.id" />" >
					<td><s:property value="#state.status.name()" /></td>
					<td><s:property value="#state.name.toString()" /></td>
                    <td class="center"><s:checkbox name="contractorCanEdit" value="#state.contractorCanEdit" /></td>
                    <td class="center"><s:checkbox name="operatorCanEdit" value="#state.operatorCanEdit" /></td>
                    <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER%>">
                        <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>WorkflowState.<s:property value='id' />.name" target="_BLANK">
                            <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
                        </a></td>
                    </pics:toggle>
                    <pics:toggleElse>
                        <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE%>">
                            <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>WorkflowState.<s:property value='id' />.name" target="_BLANK">
                                <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
                            </a></td>
                        </pics:toggle>
                        <pics:toggleElse>
                        <td><a class="edit translate" href="ManageTranslations.action?button=Search&amp;key=WorkflowState.<s:property value='#state.id' />." target="_BLANK">
                                Manage Translations
                            </a></td>
                        </pics:toggleElse>
                    </pics:toggleElse>
                    <td><a href="javascript:;" class="save editStatus"></a></td>
					<td><a href="javascript:;" class="remove deleteStatus"></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<br />
<h4>Workflow Steps</h4>
<s:if test="workFlow.steps.size() > 0" >
<table class="report" id="workflowStepsTable">
	<thead>
			<th>Old Status</th>
			<th>New Status</th>
			<th>Email Template</th>
			<th>Note Required</th>
            <th>Action</th>
			<th>Save</th>
			<th>Delete</th>
	</thead>
	<tbody>
		<s:iterator value="steps" >
			<tr id="step_<s:property value="id" />">
				<td>
					<s:select
						list="@com.picsauditing.jpa.entities.AuditStatus@getValuesWithDefault()"
						name="oldStatus"
					/>
				</td>
				<td>
					<s:select
						list="@com.picsauditing.jpa.entities.AuditStatus@values()"
						listValue="name()"
						listKey="name()"
						name="newStatus"
					/>
				</td>
				<td>
					<s:select
						list="emailTemplates"
						listValue="templateName"
						name="emailTemplateID"
						value="emailTemplate.id"
						listKey="id"
						headerKey="0"
						headerValue="- Select A Template -"
					/>
					<s:if test="emailTemplate.id > 0">
						<s:url var="email_template_link" action="EditEmailTemplate">
							<s:param name="type" value="%{emailTemplate.listType}" />
							<s:param name="templateID" value="%{emailTemplate.id}" />
						</s:url>
						<a href="${email_template_link}" class="go">Go</a>
					</s:if>
				</td>
				<td class="center"><s:checkbox name="noteRequired" /></td>
                <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER%>">
                    <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>WorkflowStep.<s:property value='id' />.name" target="_BLANK">
                        <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
                    </a></td>
                </pics:toggle>
                <pics:toggleElse>
                    <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE%>">
                        <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>WorkflowStep.<s:property value='id' />.name" target="_BLANK">
                            <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
                        </a></td>
                </pics:toggle>
                    <pics:toggleElse>
                        <td><a class="edit translate" href="ManageTranslations.action?button=Search&amp;key=WorkflowStep.<s:property value='id' />." target="_BLANK">
                            Manage Translations
                        </a></td>
                    </pics:toggleElse>
                </pics:toggleElse>
				<td><a href="javascript:;" class="save editStep"></a></td>
				<td><a href="javascript:;" class="remove deleteStep"></a></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
	</s:if>
</div>
<br />
<pics:permission perm="ManageAuditWorkFlow" type="Edit">
	<div class="workflow_status_add_area">
		<a href="javascript:;" class="add showAddStatus">Add Workflow State</a>
		<s:form id="form_status" cssStyle="display: none" >
			<fieldset class="form">
			<s:hidden name="id" value="%{id}" />
			<s:hidden name="button" value="AddStatus" />
				<h2 class="formLegend">Add Workflow State</h2>
				<ol>
					<li>
						<label>Status:</label>
						<s:select
							list="@com.picsauditing.jpa.entities.AuditStatus@values()"
							name="status"
						/>
					</li>
					<li>
						<label>Name:</label> 
						<s:textfield name="label" />
					</li>
                    <li>
                        <label>Contractor Can Edit</label>
                        <s:checkbox name="contractorCanEdit" />
                    </li>
                    <li>
                        <label>Operator Can Edit</label>
                        <s:checkbox name="operatorCanEdit" />
                    </li>
				</ol>
			</fieldset>
			<br clear="all" />
			<fieldset class="forms submit">
				<input type="button" value="Add" name="button" class="picsbutton positive addStatus" />
				<input type="button" value="Cancel" class="hide-form-status picsbutton negative" />
			</fieldset>
		</s:form>
	</div>
</pics:permission>

<pics:permission perm="ManageAuditWorkFlow" type="Edit">
	<div class="workflow_step_add_area">
		<a href="javascript:;" class="add showAddStep">Add Workflow Step</a>
		<s:form id="form_steps" cssStyle="display: none">
			<fieldset class="form">
			<s:hidden name="id" value="%{id}" />
			<s:hidden name="button" value="Add" />
				<h2 class="formLegend">Add Workflow Step</h2>
				<ol>
					<li>
						<label>Old Status:</label>
						<s:select
							list="@com.picsauditing.jpa.entities.AuditStatus@getValuesWithDefault()"
							name="oldStatus"
						/>
					</li>
					<li>
						<label>New Status:</label>
						<s:select
							list="@com.picsauditing.jpa.entities.AuditStatus@values()"
							listKey="name()"
							listValue="name()"
							name="newStatus"
						/>
					</li>
					<li>
						<label>Email Template:</label>
						<s:select
							list="emailTemplates"
							listValue="templateName"
							name="emailTemplateID"
							listKey="id"
							headerKey="0"
							headerValue="- Select A Template -"
						/>
					</li>
					<li>
						<label>Note Required</label>
						<s:checkbox name="noteRequired" />
					</li>
				<li>
					<label>Button Name:</label>
					<s:textfield name="label"  />
				</li>
				<li>
					<label>Help Text:</label>
					<s:textfield name="helpText"  />
				</li>
				</ol>
			</fieldset>
			<br clear="all" />
			<fieldset class="forms submit">
				<input type="button" value="Add" name="button" class="picsbutton positive addStep" />
				<input type="button" value="Cancel" class="hide-form-steps picsbutton negative" />
			</fieldset>
		</s:form>
	</div>
</pics:permission>