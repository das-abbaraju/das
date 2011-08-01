<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Licenses</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor Licenses</h1>

<s:include value="filters.jsp" />

<div class="right">
	<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportContractorLicenses');" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"><s:text name="global.Download" /></a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><s:text name="global.ContractorName" /></td>
		<td>PQF</td>
		<s:if test="filter.primaryInformation">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
		</s:if>
		<s:if test="permissions.operator">
			<td><s:text name="global.Flag" /></td>
		</s:if>
		<td colspan="2">CA License</td>
		<td>License Comments</td>
		<td colspan="2">Expiration</td>
		<td>Expiration Comments</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
			<td class="icon center">
				<a href="Audit.action?auditID=<s:property value="get('auditID')"/>" style="icon"><img
				src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
			</td>
			<s:if test="filter.primaryInformation">
				<td><s:property value="[0].get('contactname')"/></td>
				<td><s:property value="[0].get('contactphone')"/></td>
				<td><s:property value="[0].get('contactemail')"/></td>
			</s:if>
			<s:if test="permissions.operator">
			<td class="center">
				<a href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>" title="Click to view Flag Color details">
				<img src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
			</td>
			</s:if>
		<s:if test="[0].get('dateVerified401') != NULL">
			<td>
				<s:property value="[0].get('answer401')"/>
			</td>
			<td>
				<img src="images/okCheck.gif" width="19" height="15" />
			</td>
		</s:if>
		<s:else>
			<td colspan="2"><s:property value="[0].get('answer401')"/></td>
		</s:else>
		<td><s:property value="[0].get('comment401')" escape="false"/></td>
		<s:set name="expired" value="@com.picsauditing.PICS.DateBean@isAfterToday(get('answer755'))"/>
		<s:if test="[0].get('dateVerified401') != NULL">
			<td <s:if test="!#expired">style="color: #CC0000;"</s:if>><s:property value="[0].get('answer755')"/></td>
			<td><img src="images/okCheck.gif" width="19" height="15" /></td>
		</s:if>
		<s:else>
			<td colspan="2" <s:if test="!#expired">style="color: #CC0000;"</s:if>><s:property value="[0].get('answer755')"/></td>
		</s:else>
		<td><s:property value="[0].get('comment755')" escape="false"/></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
