<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" id="requestedContractor">
	<thead>
		<tr>
			<td><s:text name="%{scope}.header.RequestedContractor" /></td>
			<td><s:text name="%{scope}.header.RequestedBy" /></td>
			<td><s:text name="%{scope}.header.Deadline" /></td>
			<td><s:text name="%{scope}.header.LastContacted" /></td>
		</tr>
	</thead>
	<s:if test="requestedContractors.size  == 0">
		<tr><td colspan="4"><s:text name="%{scope}.message.NoOpenRequests" /></td></tr>
	</s:if>
	<s:else>
	<s:iterator value="requestedContractors">
		<tr>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td>
				<s:if test="permissions.operator">
					<s:property value="requestedByUser == null ? requestedByUserOther : requestedByUser.name" />
				</s:if>
				<s:else><s:property value="requestedBy.name" /></s:else>
			</td>
			<td><nobr><s:property value="maskDateFormat(deadline)"/></nobr></td>
			<td class="call"><nobr><s:property value="maskDateFormat(lastContactDate)"/></nobr></td>
		</tr>
	</s:iterator>
	</s:else>
</table>
<s:if test="permissions.operatorCorporate && returnedContractors.size > 0">
	<table class="report" style="width: 100%">
		<thead>
			<tr>
				<th><s:text name="%{scope}.header.ReturnedRequests" /></th>
				<th><s:text name="%{scope}.header.RequestedBy" /></th>
				<th><s:text name="%{scope}.header.LastContacted" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="returnedContractors">
				<tr>
					<td><a href="RequestNewContractor.action?requestID=<s:property value="id"/>"><s:property value="name" /></a></td>
					<td><s:property value="requestedByUser == null ? requestedByUserOther : requestedByUser.name" /></td>
					<td><nobr><s:property value="maskDateFormat(lastContactDate)"/></nobr></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<a href="ReportNewRequestedContractor.action" class="preview">See all Open Registration Requests</a>
</s:if>