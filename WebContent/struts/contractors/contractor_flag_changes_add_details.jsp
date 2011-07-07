<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Last Recalculation</th>
			<th>Priority</th>
			<th>Next Run Estimate</th>
			<th>Base Line Approved</th>
			<th>Base Line Approver</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td><s:property value="lastRecalc" /></td>
			<td><s:property value="priority"/></td>
			<td><s:property value="eta"/></td>
			<td><s:property value="baseLineApproved"/></td>
			<td><s:property value="baseLineApprover"/></td>
		</tr>	
	</tbody>	
</table>