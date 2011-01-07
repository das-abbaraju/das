<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ContractorList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><a
				href="?orderBy=name ASC">Contractor Name</a></td>
		<pics:permission perm="AllContractors">
			<td></td>
		</pics:permission>
		<pics:permission perm="ContractorDetails">
			<td></td>
			<s:if test="pqfVisible">
				<td>PQF</td>
			</s:if>
		</pics:permission>
		<s:if test="permissions.operatorCorporate">
			<td><a
				href="?orderBy=flag DESC">Flag</a></td>
			<s:if test="permissions.operator">
				<td>Waiting On</td>
			</s:if>
			<s:if test="operatorAccount.approvesRelationships.isTrue()">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
		</s:if>
		<pics:permission perm="PicsScore">
			<td>Score</td>
		</pics:permission>
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
			<td>Self Performed</td>
			<td>Sub Contracted</td>			
		</s:if>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><pics:permission perm="ContractorDetails"><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>">
					</pics:permission><s:property value="get('name')"/><pics:permission perm="ContractorDetails"></a></pics:permission></td>
			<pics:permission perm="AllContractors">
				<td><a
					href="ContractorEdit.action?id=<s:property value="get('id')"/>"
					>Edit</a></td>
			</pics:permission>
			<pics:permission perm="ContractorDetails">
				<td><a
					href="ContractorDocuments.action?id=<s:property value="get('id')"/>">Audits</a></td>
					<s:if test="pqfVisible">
						<td class="icon center">
								<a href="Audit.action?auditID=<s:property value="get('ca1_auditID')"/>" style="icon"><img
									src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
						</td>
					</s:if>
			</pics:permission>
			<s:if test="permissions.operatorCorporate">
				<td class="center">
					<pics:permission perm="ContractorDetails"><a href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
						title="<s:property value="get('flag')"/> - Click to view details"></pics:permission><img 
						src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0"><pics:permission perm="ContractorDetails"></a></pics:permission>
				</td>
				<s:if test="permissions.operator">
					<td>
						<pics:permission perm="ContractorDetails">
							<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" >
						</pics:permission>
						<s:property value="@com.picsauditing.jpa.entities.WaitingOn@fromOrdinal(get('waitingOn'))"/>
						<pics:permission perm="ContractorDetails"></a></pics:permission>
					</td>
				</s:if>
				<s:if test="operatorAccount.approvesRelationships.isTrue()">
					<pics:permission perm="ViewUnApproved">
						<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
							value="get('workStatus')" />
						</td>
					</pics:permission>
				</s:if>
			</s:if>
			<pics:permission perm="PicsScore">
				<td><s:property value="get('score')"/></td>
			</pics:permission>
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
				<td><s:property value="get('tradesSelf')"/></td>
				<td><s:property value="get('tradesSub')"/></td>			
			</s:if>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
