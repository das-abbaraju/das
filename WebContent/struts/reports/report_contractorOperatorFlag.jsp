<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Operator Flag</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:include value="filters.jsp" />
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportContractorOperatorFlag');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<td>Contractor</td>
			<td>Operator</td>
			<td>Flag</td>
			<s:if test="showContact">
				<td>Primary Contact</td>
				<td>Phone</td>
				<td>Email</td>
				<td>Office Address</td>
				<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
				<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
				<td>Zip</td>
				<td>Web_URL</td>
			</s:if>
			<s:if test="showTrade">
				<td>Trade</td>
				<td>Industry</td>			
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><a
					href="ContractorView.action?id=<s:property value="get('id')"/>"
					><s:property value="get('name')" /></a>
				</td>
				<td><s:property value="get('opName')"/></td>
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>" 
						title="<s:property value="get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
					<td><s:property value="get('industry')"/></td>
				</s:if>
			</tr>			
		</s:iterator>
	</tbody>		
</table>
</body>
</html>	