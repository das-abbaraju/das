<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Contractor" /></th>
			<th><s:text name="global.Date" /></th>
		</tr>
	</thead>
	<s:iterator value="pendingApprovalContractors.keySet()" id="contractor">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="#contractor.id"/>"><s:property value="#contractor.name" /></a></td>
			<td class="center"><s:date name="pendingApprovalContractors.get(#contractor)" format="%{getText('MonthAndDayTime')}" /></td>
		</tr>
	</s:iterator>
</table>
