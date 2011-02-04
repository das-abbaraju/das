<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<pics:permission perm="ManageAuditWorkFlow" type="Edit">
	<s:form id="form1" action="ManageAuditWorkFlow">
		<fieldset class="form">
			<s:hidden name="id" value="%{workFlow.id}" />
			<h2 class="formLegend"><s:if test="workFlow.id > 0">Edit</s:if><s:else>Add</s:else> New Workflow</h2>
			<ol>
				<li><label>Name:</label>
					<s:textfield name="name" value="%{workFlow.name}" />
				</li>
				<li><label>Has Requirements</label>
					<s:radio name="hasRequirements" value="workFlow.hasRequirements" theme="pics" list="#{true:'Yes', false:'No'}"/>
				</li>
			</ol>
		</fieldset>
		<br clear="all">
		<fieldset class="forms submit">
			<input type="submit" value="<s:if test="workFlow.id > 0">Edit Workflow</s:if><s:else>Create</s:else>" name="button" class="picsbutton positive" />
			<input type="button" value="Cancel" class="picsbutton negative closeEdit" />
		</fieldset>
	</s:form>		
</pics:permission>	