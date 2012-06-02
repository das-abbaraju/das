<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="report.allRows == 0">
	<div class="alert">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:if>
<s:else>
	<pics:permission perm="ContractorDetails">
		<div class="right">
			<a 
				class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
				href="javascript: download('ContractorList');" 
				title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
			><s:text name="global.Download" /></a>
		</div>
	</pics:permission>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<%-- sort by name --%>
	<s:if test="orderBy == 'name ASC'">
		<s:set var="name_filtered" value="%{'name DESC'}" />
	</s:if>
	<s:else>
		<s:set var="name_filtered" value="%{'name ASC'}" />
	</s:else>
	
	<%-- sort by filter --%>
	<s:if test="orderBy == 'flag DESC'">
		<s:set var="flag_filtered" value="%{'flag ASC'}" />
	</s:if>
	<s:else>
		<s:set var="flag_filtered" value="%{'flag DESC'}" />
	</s:else>
	
	<table class="report">
		<thead>
			<tr>
				<td colspan="2">
					<a href="?orderBy=${name_filtered}"><s:text name="global.ContractorName" /></a>
				</td>
				
				<pics:permission perm="AllContractors">
					<td></td>
				</pics:permission>
				
				<pics:permission perm="ContractorDetails">
					<s:if test="!permissions.generalContractorFree">
						<td></td>
						
						<s:if test="pqfVisible">
							<td>
								<s:text name="AuditType.1.name" />
							</td>
						</s:if>
					</s:if>
				</pics:permission>
				
				<s:if test="permissions.operatorCorporate && !permissions.generalContractorFree">
					<td>
						<a href="?orderBy=${flag_filtered}"><s:text name="global.Flag" /></a>
					</td>
					
					<s:if test="permissions.operator">
						<td>
							<s:text name="WaitingOn" />
						</td>
					</s:if>
					
					<s:if test="operatorAccount.approvesRelationships.isTrue()">
						<pics:permission perm="ViewUnApproved">
							<td>
								<s:text name="AuditStatus.Approved" />
							</td>
						</pics:permission>
					</s:if>
				</s:if>
				
				<pics:permission perm="PicsScore">
					<td>
						<s:text name="ContractorAccount.score" />
					</td>
				</pics:permission>
				
				<s:if test="showContact">
					<td>
						<s:text name="global.ContactPrimary" />
					</td>
					<td>
						<s:text name="User.phone" />
					</td>
					<td>
						<s:text name="User.email" />
					</td>
					<td>
						<s:text name="ContractorList.label.OfficeAddress" />
					</td>
					<td>
						<a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a>
					</td>
					<td>
						<a href="javascript: changeOrderBy('form1','a.state,a.name');"><s:text name="State" /></a>
					</td>
					<td>
						<s:text name="global.ZipPostalCode" />
					</td>
					<td>
						<s:text name="ContractorAccount.webUrl" />
					</td>
				</s:if>
				
				<s:if test="showTrade">
					<td>
						<s:text name="Trade" />
					</td>
					<td>
						<s:text name="ContractorAccount.tradesSelf" />
					</td>
					<td>
						<s:text name="ContractorAccount.tradesSub" />
					</td>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td class="right">
						<s:property value="#stat.index + report.firstRowNumber" />
					</td>
					<td>
						<pics:permission perm="ContractorDetails">
							<a 
								href="ContractorView.action?id=<s:property value="get('id')"/>" 
								rel="ContractorQuick.action?id=<s:property value="get('id')"/>" 
								class="contractorQuick account<s:property value="get('status')"/>" 
								title="<s:property value="get('name')"/>">
						</pics:permission>
						
						<s:property value="get('name')"/>
							
						<pics:permission perm="ContractorDetails"></a></pics:permission>
						
						<s:if test="get('dbaName') != null && get('dbaName').toString().length() > 0 && get('name') != get('dbaName')">
							<div class="dba">
								<s:property value="get('dbaName')"/>
							</div>
						</s:if>
						
						<s:if test="permissions.generalContractor && get('nonGCOpsInCommon') == 0">
							<div class="right">
								<a href="javascript:;" class="help" title="<s:text name="ContractorList.PleaseAddOperatorsToSub" />"></a>
							</div>
						</s:if>
						
					</td>
					
					<pics:permission perm="AllContractors">
						<td>
							<a href="ContractorEdit.action?id=<s:property value="get('id')"/>"><s:text name="button.Edit" /></a>
						</td>
					</pics:permission>
					
					<pics:permission perm="ContractorDetails">
						<s:if test="!permissions.generalContractorFree">
							<td>
								<a href="ContractorDocuments.action?id=<s:property value="get('id')"/>"><s:text name="ContractorList.label.Audits" /></a>
							</td>
								
							<s:if test="pqfVisible">
								<td class="icon center">
									<a href="Audit.action?auditID=<s:property value="get('ca1_auditID')"/>" style="icon">
										<img src="images/icon_PQF.gif" width="20" height="20" border="0">
									</a>
								</td>
							</s:if>
						</s:if>
					</pics:permission>
					
					<s:if test="permissions.operatorCorporate && !permissions.generalContractorFree">
						<td class="center">
							<pics:permission perm="ContractorDetails">
								<s:if test='get("forceEnd") != null'>
								<a 
									href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
									title="<s:property value="get('flag')"/>: Forced until <s:date name="get('forceEnd')" format="%{getText('date.short')}" /> - Click to view details">
								</s:if>
								<s:elseif test='get("dataForceEnd") != null'>
								<a 
									href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
									title="<s:property value="get('flag')"/>: Forced until <s:date name="get('dataForceEnd')" format="%{getText('date.short')}" /> - Click to view details">
								</s:elseif>
								<s:else>
								<a 
									href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
									title="<s:property value="get('flag')"/> - Click to view details" >
								</s:else>
							</pics:permission>
							
							<img src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0">
							
							
							<pics:permission perm="ContractorDetails">
								<s:if test='get("forceEnd") != null || get("dataForceEnd") != null'>*</s:if></a>
							</pics:permission>
						</td>
						<s:if test="permissions.operator && !permissions.generalContractorFree">
							<td>
								<pics:permission perm="ContractorDetails">
									<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" >
								</pics:permission>
								
								<s:text name="%{@com.picsauditing.jpa.entities.WaitingOn@fromOrdinal(get('waitingOn')).i18nKey}" />
								
								<pics:permission perm="ContractorDetails">
									</a>
								</pics:permission>
							</td>
						</s:if>
						
						<s:if test="operatorAccount.approvesRelationships.isTrue()">
							<pics:permission perm="ViewUnApproved">
								<td class="center">
									<s:property value="get('workStatus')" />
								</td>
							</pics:permission>
						</s:if>
					</s:if>
					
					<pics:permission perm="PicsScore">
						<td>
							<s:property value="get('score')"/>
						</td>
					</pics:permission>
					
					<s:if test="showContact">
						<td>
							<s:property value="get('contactname')"/>
						</td>
						<td>
							<s:property value="get('contactphone')"/>
						</td>
						<td>
							<s:property value="get('contactemail')"/>
						</td>
						<td>
							<s:property value="get('address')"/>
						</td>
						<td>
							<s:property value="get('city')"/>
						</td>
						<td>
							<s:property value="get('state')"/>
						</td>
						<td>
							<s:property value="get('zip')"/>
						</td>
						<td>
							<s:property value="get('web_URL')"/>
						</td>
					</s:if>
					
					<s:if test="showTrade">
						<td>
							<s:property value="get('main_trade')"/>
						</td>
						<td>
							<s:property value="get('tradesSelf')"/>
						</td>
						<td>
							<s:property value="get('tradesSub')"/>
						</td>			
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:else>