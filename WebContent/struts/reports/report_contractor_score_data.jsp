<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportContractorScore');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</s:if>
</pics:permission>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=a.name" ><s:text name="global.Contractor" /></a></th>
	    <th><a href="javascript: changeOrderBy('form1','regYear');"><s:text name="ReportContractorScore.header.RegistrationYear" /></a></th>
	    <th><s:text name="ReportContractorScore.header.AuditStatus"><s:param><s:text name="AuditType.195.name" /></s:param></s:text></th>
	    <th><a href="javascript: changeOrderBy('form1','195Score');"><s:text name="ReportContractorScore.header.Score" /></a></th>
	    <th><a href="javascript: changeOrderBy('form1','195Updated');"><s:text name="ReportContractorScore.header.StatusUpdated" /></a></th>
	    <th><s:text name="ReportContractorScore.header.AuditStatus"><s:param><s:text name="AuditType.196.name" /></s:param></s:text></th>
	    <th><a href="javascript: changeOrderBy('form1','196Updated');"><s:text name="ReportContractorScore.header.StatusUpdated" /></a></th>
		<s:if test="showContact">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
			<td><s:text name="ReportContractorScore.header.OfficeAddress" /></td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
			<td><s:text name="global.ZipPostalCode" /></td>
			<td><s:text name="ReportContractorScore.header.WebURL" /></td>
		</s:if>
		<s:if test="showTrade">
			<td><s:text name="ReportContractorScore.header.Trade" /></td>
		</s:if>	    
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="get('regYear')"/></td>
			<td>
				<a href="Audit.action?auditID=<s:property value="get('195ID')" />">
					<s:text name="AuditStatus.%{get('195Status')}" />
				</a>
			</td>
			<td class="right"><s:property value="get('195Score')" /></td>
			<td><s:date name="parseDate(get('195Updated'))" /></td>
			<td>
				<a href="Audit.action?auditID=<s:property value="get('196ID')" />">
					<s:text name="AuditStatus.%{get('196Status')}"/>
				</a>
			</td>
			<td><s:date name="parseDate(get('196Updated'))" /></td>
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