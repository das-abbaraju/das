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
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.name');">Contractor Name</a></td>
	    <td><a href="javascript: changeOrderBy('form1','state, city');">Address</a></td>
	    <td><a href="javascript: changeOrderBy('form1','contact');">Contact</a></td>
	    <td><a href="javascript: changeOrderBy('form1','phone');">Phone</a></td>
		<s:if test="permissions.operator">
			<td><a href="javascript: changeOrderBy('form1','flag DESC, a.name');">Flag</a></td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
		</s:if>
		<td>Action</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr <s:if test="!(get('genID') > 0)">class="notapp"</s:if>>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:if test="get('genID') > 0"><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></s:if>
				<s:else><s:property value="[0].get('name')" /></s:else></td>
			<td><s:property value="[0].get('city')"/>, <s:property value="[0].get('state')"/></td>
			<td><s:property value="[0].get('contact')"/></td>
			<td><s:property value="[0].get('phone')"/><br />
			<s:property value="[0].get('phone2')"/></td>
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
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
