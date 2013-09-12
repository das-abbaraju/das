<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="javascript: download('ReportIncidenceRate');" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
			<s:text name="global.Download" />
		</a>
	</div>
</s:if>
</pics:permission>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.Contractor" /></a></th>
			<td><s:text name="global.Type" /></td>
			<td><s:text name="ReportIncidenceRate.Rate" /></td>
			<td><s:text name="Filters.label.ForYear" /></td>
		    <th><a href="javascript: changeOrderBy('form1','c.trirAverage');"><s:text name="global.Average" /></a></th>
			<td><s:text name="ReportIncidenceRate.TrirIndustryAverage" /></td>
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
	<!--TODO Add in the Contractor FlagColor-->
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<td><s:property value="[0].get('shaType')" /></td>
			<!--Need to fix this before the year end-->
			<td class="right">
				<s:property	value="get('incidenceRate')" />
			</td>
			<td><s:property value="get('auditFor')" /></td>
			<td><s:property value="get('trirAverage')" /></td>
		    <td><s:property value="[0].get('trir')" /></td>
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

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
