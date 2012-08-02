<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
	<s:text name="DelinquentContractorAccounts.help.DeactivatedInNextFewDays" />
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2"><s:text name="global.ContractorName" /></td>
		<td><s:text name="DelinquentContractorAccounts.label.DueDate" /></td>
		<s:if test="permissions.admin">
			<td><s:text name="DelinquentContractorAccounts.label.Amount" /></td>
			<td><s:text name="DelinquentContractorAccounts.label.FacilityCount" /></td>
		</s:if>
		<td><s:text name="DelinquentContractorAccounts.label.DaysLeft" /></td>
		<s:if test="showContact">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
			<td><s:text name="global.OfficeAddress" /></td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
			<td><s:text name="global.ZipPostalCode" /></td>
			<td><s:text name="ContractorAccount.webUrl" /></td>
		</s:if>
		<s:if test="showTrade">
			<td><s:text name="Trade" /></td>
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
			<td class="center"><s:date name="[0].get('dueDate')" /></td>
			<s:if test="permissions.admin">
				<td><s:property value="get('invoiceAmount')"/></td>
				<td><s:property value="get('facilityCount')"/></td>
			</s:if>
			<td class="center"><s:property value="[0].get('DaysLeft')" /></td>
			<s:if test="showContact">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('countrySubdivision')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>