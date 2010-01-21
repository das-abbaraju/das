<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Search for New Contractors</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Search for New Contractors</h1>
<s:include value="filters.jsp" />

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('NewContractorSearch');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="info">
	Green flagged contractors have completed the PQF and Annual Updates. If your facility has more requirements, their flag will change once you add them. 
</div>

<table class="report">
	<thead>
	<tr>
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.name');">Contractor Name</a></td>
		<s:if test="permissions.operator">
			<td><a href="javascript: changeOrderBy('form1','flag DESC, a.name');">Flag</a></td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
		</s:if>
		<td>Action</td>
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
	<s:iterator value="data" status="stat">
		<tr <s:if test="!(get('genID') > 0)">class="notapp"</s:if>>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:if test="get('genID') > 0"><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></s:if>
				<s:else><s:property value="[0].get('name')" /></s:else></td>
			<s:if test="permissions.operator">
				<td class="center"><img 
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0" /></td>
				<s:if test="operatorAccount.approvesRelationships">
					<pics:permission perm="ViewUnApproved">
						<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
							value="[0].get('workStatus')" />
						</td>
					</pics:permission>
				</s:if>
			</s:if>
			<td class="center">
				<s:if test="get('genID') > 0">
					<pics:permission perm="RemoveContractors">
						<s:if test="permissions.corporate">
							<a href="ContractorFacilities.action?id=<s:property value="[0].get('id')"/>">Remove</a>
						</s:if>
						<s:else>
							<a href="?button=remove&id=<s:property value="[0].get('id')"/>">Remove</a>
						</s:else>
					</pics:permission>
				</s:if>
				<s:else>
					<pics:permission perm="AddContractors">
						<s:if test="permissions.corporate">
							<a href="ContractorFacilities.action?id=<s:property value="[0].get('id')"/>">Add</a>
						</s:if>
						<s:else>
							<a href="?button=add&id=<s:property value="[0].get('id')"/>">Add</a>
						</s:else>
					</pics:permission>
				</s:else>
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
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
</body>
</html>
