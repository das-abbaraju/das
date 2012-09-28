<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ArchivedContractorAccounts.title" /></title>
<s:include value="reportHeader.jsp" /></head>
<body>
<h1><s:text name="ArchivedContractorAccounts.title" /></h1>

<s:include value="filters.jsp" />
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge && data.size() > 0">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ArchivedAccounts');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</s:if>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<div class="info">
	<s:text name="ArchivedContractorAccounts.message.PicsMembershipLapsed" />
</div>
<s:if test="data.size > 0">
<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2"><s:text name="global.ContractorName" /></td>
		<s:if test="permissions.admin">
			<td><a href="javascript: changeOrderBy('form1','a.creationDate');"><s:text name="ArchivedContractorAccounts.label.CreatedOn" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','c.paymentExpires');"><s:text name="AuditStatus.Expired" /></a></td>
			<td><s:text name="Filters.header.DeactivationReason" /></td>
			<td><s:text name="global.SafetyRisk" /></td>
			<td><s:text name="global.ProductRisk" /></td>
			<td><s:text name="ArchivedContractorAccounts.label.NumberOfEmployees" /></td>
		</s:if>
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
		<pics:permission perm="RemoveContractors">
		<s:if test="permissions.operator">
			<td><s:text name="button.Remove" /></td>
		</s:if>
		</pics:permission>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
			<s:if test="permissions.admin">
				<a href="ContractorView.action?id=<s:property value="get('id')"/>">
				<s:property value="get('name')" /></a>
			</s:if>
			<s:else>
				<s:property value="get('name')" />
			</s:else>
			</td>
			<s:if test="permissions.admin">
				<td><s:date name="get('creationDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
				<td><s:date name="get('paymentExpires')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
				<td><s:property value="reasons.get(get('reason'))" /></td>
				<td>
					<s:if test="get('safetyRisk') != null">
						<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('safetyRisk'))" />
					</s:if>
				</td>
				<td>
					<s:if test="get('productRisk') != null">
						<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('productRisk'))" />
					</s:if>
				</td>
				<td><s:property value="get('answer69')" /></td>
			</s:if>
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
			<pics:permission perm="RemoveContractors">
				<s:if test="permissions.operator">
				<td>
				<s:form action="ArchivedContractorAccounts" method="POST">
					<s:hidden value="%{get('id')}" name="contractor" />
					<s:hidden value="%{permissions.accountId}" name="operator" />
					<s:submit value="%{getText('button.Remove')}" method="remove" cssClass="picsbutton negative" />
				</s:form>
				</td>
				</s:if>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:if>

</body>
</html>
