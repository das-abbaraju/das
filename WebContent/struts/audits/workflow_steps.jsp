<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="steps.size() > 0">
<table class="report" id="workflowStepsTable">
	<thead>
		<tr>
			<th>Old Status</th>
			<th>New Status</th>
			<th>Email Template</th>
			<th>Note Required</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="steps" >
			<tr id="step_<s:property value="id" />">
				<td><s:property value="oldStatus" /></td>
				<td><s:property value="newStatus" /></td>
				<td>
					<s:property value="emailTemplate.templateName" />
					<s:if test="emailTemplate.id > 0">
						<a href="EditEmailTemplate.action?#template=<s:property value="emailTemplate.id" />" class="go" >Go</a>
					</s:if>
				</td>
				<td>
					<s:if test="noteRequired">Yes</s:if>
					<s:else>No</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>