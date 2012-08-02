<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
	<div class="right">
		<a class="excel" href="javascript: download('EmailWebinar');" 
			<s:if test="report.allRows > 500">onclick="return confirm(<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>);"</s:if>
			title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
			<s:text name="global.Download" />
		</a>
	</div>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><a href="?orderBy=name ASC"><s:text name="global.ContractorName" /></a></td>
		<pics:permission perm="AllContractors">
			<td></td>
		</pics:permission>
		<pics:permission perm="ContractorDetails">
			<td></td>
			<s:if test="pqfVisible">
				<td><s:text name="AuditType.1.name" /></td>
			</s:if>
		</pics:permission>
		<s:if test="permissions.operatorCorporate">
			<td><a href="?orderBy=flag DESC"><s:text name="global.Flag" /></a></td>
			<s:if test="permissions.operator">
				<td><s:text name="WaitingOn" /></td>
			</s:if>
			<s:if test="operatorAccount.approvesRelationships.isTrue()">
				<pics:permission perm="ViewUnApproved">
					<td><nobr><s:text name="AuditStatus.Approved" /></nobr></td>
				</pics:permission>
			</s:if>
		</s:if>
		<pics:permission perm="PicsScore">
			<td><s:text name="ContractorAccount.score" /></td>
		</pics:permission>
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
			<td><s:text name="ReportEmailWebinar.header.SelfPerformed" /></td>
			<td><s:text name="ReportEmailWebinar.header.SubContracted" /></td>			
		</s:if>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><pics:permission perm="ContractorDetails"><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuick.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>">
					</pics:permission><s:property value="get('name')"/>
					<pics:permission perm="ContractorDetails"></a></pics:permission>
						<s:if test="get('dbaName') != null && get('dbaName').toString().length() > 0 && get('name') != get('dbaName')">
							<div class="dba"><s:property value="get('dbaName')"/></div>
						</s:if>
			</td>
			<pics:permission perm="AllContractors">
				<td><a href="ContractorEdit.action?id=<s:property value="get('id')"/>"><s:text name="button.Edit" /></a></td>
			</pics:permission>
			<pics:permission perm="ContractorDetails">
				<td><a href="ContractorDocuments.action?id=<s:property value="get('id')"/>"><s:text name="ReportEmailWebinar.Audits" /></a></td>
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
				<td><s:property value="get('countrySubdivision')"/></td>
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
