<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="data.size > 0">
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="table insurance-approval-report">
		<thead>
			<tr>
				<th title="<s:text name="ReportInsuranceApproval.PICSRecommendation" />" style="cursor: help;"></th>
				<th>
					<a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.Contractor" /></a>
				</th>
				<th>
					<a><s:text name="ReportInsuranceApproval.Policy" /></a>
				</th>
				<th align="center">
					<a href="javascript: changeOrderBy('form1','expiresDate ASC');"><s:text name="ReportInsuranceApproval.Expires" /></a>
				</th>
				
				<s:if test="filter.primaryInformation">
					<th>
						<s:text name="global.Contact" />
					</th>
				</s:if>
				
				<th>
					<s:text name="ReportInsuranceApproval.Limits" />
				</th>
				<th>
					<s:text name="AmBest" />
				</th>
				<th title="<s:text name="ReportInsuranceApproval.AdditionalRequirements" />">
					<s:text name="ReportInsuranceApproval.AdditionalShort" />
				</th>
				<th>
					<s:text name="ReportInsuranceApproval.Cert" />
				</th>
				<th>
					<s:text name="global.Notes" />
				</th>
                <th>
                    Actions
                </th>
			</tr>
		</thead>
		
		<s:iterator value="data" status="rowstatus">
			<tr class="<s:if test="#rowstatus.odd == true">odd</s:if><s:else>even</s:else>" data-cao-id="<s:property value="get('caoId')" />">
				<td>
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
					
					<s:date name="get('expiresDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
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
						<s:property value="getFormattedDollarAmount(answer)"/> =
						<span><s:property value="question.name.toString()"/></span>
						<br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'AMBest')">
						<s:property value="getAMBestRatings(comment)" escape="false"/>
					</s:iterator>
				</td>
				<td>
					<s:if test="get('valid').toString() == 'Yes'">
						<s:text name="YesNo.Yes" />
					</s:if>
					<s:else>
						<s:text name="YesNo.No" />
					</s:else>
				</td>
				<td>
					<s:if test="get('certID') != null">
						<a 
                            href="CertificateUpload.action?id=<s:property value="get('id')"/>&certID=<s:property value="get('certID')"/>&button=download"
                            target="_BLANK"
                        ><img src="images/icon_insurance.gif" /></a>	
					</s:if>
				</td>
				<td>
					<s:property value="get('caoNotes')"/>
				</td>
                <td class="actions">
                    <ul>
                        <li class="change-policy">
                            <a href="javascript:;" class="btn small success policy-approve"><s:text name="AuditStatus.Approved.button" /></a>
                        </li>
                        <li class="change-policy">
                            <a href="javascript:;" class="btn small danger policy-reject"><s:text name="button.Reject" /></a>
                        </li>
                        <li class="change-policy">
                            <a href="javascript:;" class="btn small policy-na"><s:text name="AuditStatus.NotApplicable.button" /></a>
                        </li>
                        <li class="revert-policy">
                            <a href="javascript:;" class="btn small policy-revert"><s:text name="button.Undo" /></a>
                        </li>
                    </ul>
                </td>
			</tr>
		</s:iterator>
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
