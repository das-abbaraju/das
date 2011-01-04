<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria.
	Please try again.</div>
</s:if>
<s:else>
	<div><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th>Type</th>
				<th>Id</th>
				<th>Status</th>
				<th>Date</th>
				<th>Changed By</th>
				<th>View</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data">
				<tr>
					<td><s:property value="get('rType')"/></td>
					<td><s:property value="get('id')"/></td>
					<td><s:property value="get('status')"/></td>
					<td><s:date name="get('sDate')" format="MM/dd/yyyy -  HH:MM" /></td>
					<td><s:property value="get('who')"/></td>
					<td><a class="go" href="<s:property value="get('rType')"/>Editor.action?id=<s:property value="get('id')"/>">Go</a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div class="alphapaging"><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
</s:else>