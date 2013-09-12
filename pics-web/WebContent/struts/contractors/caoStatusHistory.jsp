<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<input class="picsbutton negative" type="button" value="Close" id="noButton" />
<table class="report">
	<thead>
		<tr>
			<th><s:text name="Audit.header.When" /></th>
			<th><s:text name="Audit.header.WhoChanged" /></th>
			<th><s:text name="Audit.header.OldStatus" /></th>
			<th><s:text name="Audit.header.NewStatus" /></th>
			<th><s:text name="global.Notes" /></th>
		</tr>
	</thead>
	<tbody>
		<s:if test="caoWorkflow.size() > 0">
			<s:iterator value="caoWorkflow" var="caow">
				<tr id=<s:property value="id"/>>
					<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}"/></td>
					<td><s:property value="updatedBy.name"/></td>
					<td><s:text name="%{#caow.previousStatus.getI18nKey()}" /></td>
					<td><s:text name="%{#caow.status.getI18nKey()}"/></td>
					<td><div class="ac_cao_notes"><s:property value="mappedNote" escape="false"/></div>
						<s:if test="permissions.userId == updatedBy.id"><a class="editNote showPointer edit"><s:text name="button.Edit" /></a></s:if>
					</td>
				</tr>
			</s:iterator>	
		</s:if>
		<s:else>
			<tr>
				<td colspan="5"><s:text name="Audit.message.NoStatusChanges" /></td>
			</tr>
		</s:else>
	</tbody>
</table>
