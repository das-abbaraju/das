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
	<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportContractorLicenses');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>PQF</td>
		<s:if test="filter.primaryInformation">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Email</td>
		</s:if>
		<s:if test="permissions.operator">
			<td>Flag</td>
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
			<td>
				<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditStatus')"/></a>
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
