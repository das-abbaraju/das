<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="data.size > 0">
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="report">
		<thead>
			<tr>
				<td>
					<input title="<s:text name="ReportInsuranceApproval.CheckAll" />" type="checkbox" id="setAllCheckboxes" />
				</td>
				<td title="<s:text name="ReportInsuranceApproval.PICSRecommendation" />" style="cursor: help;"></td>
				<td>
					<a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.Contractor" /></a>
				</td>
				<td>
					<a><s:text name="ReportInsuranceApproval.Policy" /></a>
				</td>
				<td align="center">
					<a href="javascript: changeOrderBy('form1','expiresDate ASC');"><s:text name="ReportInsuranceApproval.Expires" /></a>
				</td>
				
				<s:if test="filter.primaryInformation">
					<td>
						<s:text name="global.Contact" />
					</td>
				</s:if>
				
				<td>
					<s:text name="ReportInsuranceApproval.Limits" />
				</td>
				<td>
					<s:text name="AmBest" />
				</td>
				<td title="<s:text name="ReportInsuranceApproval.AdditionalRequirements" />">
					<s:text name="ReportInsuranceApproval.AdditionalShort" />
				</td>
				<td>
					<s:text name="ReportInsuranceApproval.Cert" />
				</td>
				<td>
					<s:text name="global.Notes" />
				</td>
			</tr>
		</thead>
		
		<s:iterator value="data" status="stat">
			<tr>
				<td style="text-align: center;">
					<input id="cao_cb<s:property value="get('caoId')"/>" type="checkbox" class="massCheckable" name="caoIDs" value="<s:property value="get('caoId')"/>"/>
				</td>
				<td style="text-align: center;" >
					<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('caoRecommendedFlag').toString())" escape="false"/>
				</td>
				<td>
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td>
					<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>" title="<s:text name="%{[0].get('atype.name')}" /> for <s:property value="[0].get('caoName')"/>"><s:text name="%{[0].get('atype.name')}" /></a>
					
					<s:if test="permissions.corporate">
						<br />
						<s:property value="get('caoOperatorName')"/>
					</s:if>
				</td>
				<td class="reportDate">
					<s:if test="get('auditID') == 14">
						<s:iterator value="getDataForAudit(get('auditID'),'GoodStanding')">
							<s:if test='answer == "X"'>
								<s:text name="ReportInsuranceApproval.InGoodStanding" />
								<br />
							</s:if>
						</s:iterator>
					</s:if>
					
					<s:date name="get('expiresDate')" format="M/d/yy" />
				</td>
				
				<s:if test="filter.primaryInformation">
					<td>
						<s:property value="get('contactname')"/>
						<br />
						<s:property value="get('contactphone')"/>
						<br />
						<a href="mailto:<s:property value="get('contactemail')"/>"><s:property value="get('contactemail')"/></a> <br />
					</td>
				</s:if>
				
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'Limits')">
						<nobr>
							<s:property value="getFormattedDollarAmount(answer)"/> =
							<span style="font-size: 9px;"><s:property value="question.columnHeader"/></span>
						</nobr>
						<br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'AMBest')">
						<s:property value="getAMBestRatings(comment)" escape="false"/>
					</s:iterator>
				</td>
				<td class="center">
					<s:if test="get('valid').toString() == 'Yes'">
						<s:text name="YesNo.Yes" />
					</s:if>
					<s:else>
						<s:text name="YesNo.No" />
					</s:else>
				</td>
				<td class="center">
					<s:if test="get('certID') != null">
						<a href="CertificateUpload.action?id=<s:property value="get('id')"/>&certID=<s:property value="get('certID')"/>&button=download"
						target="_BLANK"><img src="images/icon_insurance.gif" /></a>	
					</s:if>
				</td>
				<td>
					<s:property value="get('caoNotes')"/>
					<br />
				</td>
			</tr>
		</s:iterator>
		
		<tr>
			<td colspan="<s:property value="filter.primaryInformation ? 11 : 10" />">
				<div style="height:28px;">
					<s:radio 
						name="newStatuses" 
						list="#{'Approved':getText('ReportInsuranceApproval.ApproveSelected'),'Incomplete':getText('ReportInsuranceApproval.RejectSelected'),'NotApplicable':getText('ReportInsuranceApproval.MarkNA')}"
						theme="pics"
						cssClass="statusSelects inline"
					/>
				</div>
				
				<a class="picsbutton positive" href="#"><s:text name="ReportInsuranceApproval.SaveChanges" /></a>
			</td>
		</tr>
	</table>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:if>
<s:else>
	<div class="info">
		<s:text name="ReportInsuranceApproval.NoMatchingPolicies" />
	</div>
</s:else>