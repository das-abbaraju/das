<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="data.size > 0">
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
<thead>
	<tr>
		<td><input title="Check all" type="checkbox" onclick="setAllChecked(this);"/></td>
		<td title="PICS Recommendation" style="cursor: help;"></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td><a href="javascript: changeOrderBy('form1','atype.auditName');">Policy</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
		<s:if test="filter.primaryInformation">
			<td>Contact</td>
		</s:if>
		<td>Limits</td>
		<td>AMBest</td>
		<td title="Additional Requirements">Add'l</td>
		<td>Cert</td>
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
			<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>" title="<s:property value="[0].get('auditName')"/> for <s:property value="[0].get('caoName')"/>"><s:property value="[0].get('auditName')"/></a>
			<s:if test="permissions.corporate">
				<br><s:property value="get('caoOperatorName')"/>
			</s:if>
		</td>
		<td class="reportDate">
			<s:if test="get('auditName') == 'Workers Comp'">
				<s:iterator value="getDataForAudit(get('auditID'),'GoodStanding')">
					<s:if test='answer == "X"'>
						In Good Standing
						<br />
					</s:if>
				</s:iterator>
			</s:if>
			<s:date name="get('expiresDate')" format="M/d/yy" />
		</td>
		<s:if test="filter.primaryInformation">
			<td>
				<s:property value="get('contactname')"/> <br />
				<s:property value="get('contactphone')"/> <br />
				<a href="mailto:<s:property value="get('contactemail')"/>"><s:property value="get('contactemail')"/></a> <br />
			</td>
		</s:if>
		<td>
			<s:iterator value="getDataForAudit(get('auditID'),'Limits')">
				<nobr><s:property value="getFormattedDollarAmount(answer)"/> = <span style="font-size: 9px;"><s:property value="question.columnHeader"/></span></nobr>
				<br/>
			</s:iterator>
		</td>
		<td>
			<s:iterator value="getDataForAudit(get('auditID'),'AMBest')">
				<s:property value="getAMBestRatings(comment)" escape="false"/>
			</s:iterator>
		</td>
		<td class="center">
			<s:if test="get('valid').toString() == 'Yes'">Yes</s:if>
			<s:else>No</s:else>
		</td>
		<td class="center">
			<s:if test="get('certID') != null">
				<a href="CertificateUpload.action?id=<s:property value="get('id')"/>&certID=<s:property value="get('certID')"/>&button=download"
				target="_BLANK"><img src="images/icon_insurance.gif" /></a>	
			</s:if>
		</td>
	</tr>
</s:iterator>
	<tr>
		<td colspan="<s:property value="filter.primaryInformation ? 10 : 9" />">
			<div style="height:28px;">
			<s:radio cssClass="statusSelects" name="newStatuses" list="#{'Approved':'Approve Selected','Incomplete':'Reject Selected','NotApplicable':'Mark as N/A'}" />
			</div>
			<a class="picsbutton positive" href="#" onclick="return changeAuditStatus();">Save Changes</a>
		</td>
	</tr>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:if>
<s:else>
	<div class="info">There are no matching policies to review at this time.</div>
</s:else>
