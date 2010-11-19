<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<input class="picsbutton negative" type="button" value="Close" id="noButton" />
<table class="report">
	<thead>
		<tr>
			<th>When</th>
			<th>Who Changed</th>
			<th>New Status</th>
			<th>Old Status</th>
			<th>Notes</th>
		</tr>
	</thead>
	<tbody>
		<s:if test="caoWorkflow.size() > 0">
			<s:iterator value="caoWorkflow">
				<tr>
					<td><s:property value="formatDate(updateDate, 'dd MMMM yyyy')" default="N/A"/></td>
					<td><s:property value="updatedBy.name"/></td>
					<td><s:property value="status"/></td>
					<td><s:property value="previousStatus"/></td>
					<td><s:property value="notes"/></td>
				</tr>
			</s:iterator>	
		</s:if>
		<s:else>
			<tr>
				<td colspan="5">No status changes recorded</td>
			</tr>
		</s:else>
	</tbody>
</table>
