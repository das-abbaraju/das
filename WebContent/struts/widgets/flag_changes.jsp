<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<tr>
			<th>Old</th>
			<th>New</th>
			<th>Contractor</th>
			<th>Operator</th>
			<th>Last Calc</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="flagChanges" status="stat">
			<tr>
				<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('baselineFlag').toString())" escape="false"/></td>
				<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('flag').toString())" escape="false"/></td>
				<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
						rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
						class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>"
					><s:property value="get('name')"/></a></td>
				<td><a href="OperatorConfiguration.action?id=<s:property value="get('opId')"/>"><s:property value="get('opName')"/></a></td>
				<td><s:property value="get('lastRecalculation')"/> mins ago</td>
			</tr>
		</s:iterator>
		<s:if test="flagChanges.size == 0">
			<tr>
				<td colspan="5">No flag changes to report</td>
			</tr>
		</s:if>
		<tr>
			<td colspan="5" class="center"><a href="ReportFlagChanges.action?filter.conAuditorId=<s:property value="permissions.userId" />">View Report</td>
		</tr>
	</tbody>
</table>