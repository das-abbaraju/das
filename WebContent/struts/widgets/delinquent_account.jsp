<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th><s:text name="global.Contractor" /></th>
		<th><s:text name="DelinquentContractorAccounts.label.DueDate" /></th>
		<th><s:text name="DelinquentContractorAccounts.label.DaysLeft" /></th>
		</tr>
	</thead>
	<s:iterator value="delinquentContractors">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="account.id"/>"><s:property value="account.name"/></a></td>
			<td class="center"><s:date name="dueDate" /></td>
			<td class="center"><s:property value="getDaysLeft(dueDate)" /></td>
		</tr>
	</s:iterator>
	<tr>
		<td colspan="3" class="right">
			<a href="DelinquentContractorAccounts.action">
				<s:text name="DelinquentAccountsAjax.More" />
			</a>
		</td>
	</tr>
</table>
