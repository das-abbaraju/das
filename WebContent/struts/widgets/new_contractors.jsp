<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Contractor" /></th>
			<s:if test="permissions.admin">
				<th><s:text name="ContractorAccount.requestedBy" /></th>
			</s:if>
			<th>
				<s:if test="permissions.admin">
					<s:text name="RegisteredContractorsAjax.DateRegistered" />
				</s:if>
				<s:else>
					<s:text name="RegisteredContractorsAjax.DateAdded" />
				</s:else>
			</th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<s:if test="permissions.admin">
				<td><a class="account<s:property value="status" />" 
				href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name" /></a></td>
			</s:if>
			<s:elseif test="permissions.operatorCorporate">
				<td><a class="account<s:property value="status" />" 
					href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name" /></a></td>
			</s:elseif>
			<s:if test="permissions.admin">
				<td><s:property value="requestedBy.name" /></td>
			</s:if>
			<td class="center"><s:date name="creationDate" format="%{getText('date.long')}" /></td>
		</tr>
	</s:iterator>
</table>
