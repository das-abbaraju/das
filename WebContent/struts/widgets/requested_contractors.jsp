<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" id="requestedContractor">
	<thead>
		<tr>
			<td><s:text name="RequestedContractorsAjax.header.RequestedContractor" /></td>
			<td><s:text name="RequestedContractorsAjax.header.RequestedBy" /></td>
			<td><s:text name="RequestedContractorsAjax.header.Deadline" /></td>
			<td><s:text name="RequestedContractorsAjax.header.LastContacted" /></td>
		</tr>
	</thead>
	<s:if test="requestedContractors.size  == 0">
		<tr><td colspan="4"><s:text name="RequestedContractorsAjax.message.NoOpenRequests" /></td></tr>
	</s:if>
	<s:else>
	<s:iterator value="requestedContractors">
		<tr>
			<td><a href="RequestNewContractor.action?newContractor=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td>
				<s:if test="permissions.operator">
					<s:property value="requestedByUser == null ? requestedByUserOther : requestedByUser.name" />
				</s:if>
				<s:else><s:property value="requestedBy.name" /></s:else>
			</td>
			<td><nobr><s:date name="deadline"/></nobr></td>
			<td class="call"><nobr><s:date name="lastContactDate"/></nobr></td>
		</tr>
	</s:iterator>
	</s:else>
</table>
<s:if test="permissions.operatorCorporate && returnedContractors.size > 0">
	<table class="report" style="width: 100%">
		<thead>
			<tr>
				<th><s:text name="RequestedContractorsAjax.header.ReturnedRequests" /></th>
				<th><s:text name="RequestedContractorsAjax.header.RequestedBy" /></th>
				<th><s:text name="RequestedContractorsAjax.header.LastContacted" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="returnedContractors">
				<tr>
					<td><a href="RequestNewContractor.action?newContractor=<s:property value="id"/>"><s:property value="name" /></a></td>
					<td><s:property value="requestedByUser == null ? requestedByUserOther : requestedByUser.name" /></td>
					<td><nobr><s:date name="lastContactDate"/></nobr></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<a href="ReportNewRequestedContractor.action" class="preview"><s:text name="RequestedContractorsAjax.SeeAllOpenRequests" /></a>
</s:if>