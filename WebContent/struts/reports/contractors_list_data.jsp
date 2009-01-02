<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div id="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ContractorList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<pics:permission perm="AllContractors">
			<td></td>
		</pics:permission>
		<pics:permission perm="ContractorDetails">
			<td>Industry</td>
			<td>Trade</td>
			<td></td>
			<s:if test="pqfVisible">
				<td>PQF</td>
			</s:if>
		</pics:permission>
		<s:if test="permissions.operator">
			<td><a
				href="?orderBy=flag DESC">Flag</a></td>
			<td>Waiting On</td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
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
			<pics:permission perm="AllContractors">
				<td><a
					href="ContractorEdit.action?id=<s:property value="get('id')"/>"
					>Edit</a></td>
			</pics:permission>
			<pics:permission perm="ContractorDetails">
				<td><s:property value="get('industry')" /></td>
				<td><s:property value="get('main_trade')" /></td>
				<td><a
					href="ConAuditList.action?id=<s:property value="get('id')"/>">Audits</a></td>
					<s:if test="pqfVisible">
						<td align="center"><s:if test="get('ca1_auditID') > 0">
							<s:if test="get('ca1_auditStatus').equals('Exempt')">N/A</s:if>
							<s:else>
								<a
									href="Audit.action?auditID=<s:property value="get('ca1_auditID')"/>"><img
									src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
							</s:else>
							</s:if></td>
					</s:if>
			</pics:permission>
			<s:if test="permissions.operator">
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
						title="<s:property value="get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
				<td><a href="ContractorFlag.action?id=<s:property value="get('id')"/>" ><s:property value="@com.picsauditing.jpa.entities.WaitingOn@valueOf(get('waitingOn'))"/></a></td>
				<s:if test="operatorAccount.approvesRelationships">
					<pics:permission perm="ViewUnApproved">
						<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
							value="get('workStatus')" />
						</td>
					</pics:permission>
				</s:if>
			</s:if>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
