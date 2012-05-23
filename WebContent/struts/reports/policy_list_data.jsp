<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right">
		<s:if test="reports.addRows <= 40000" >
		<a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportPolicyList');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a>
		</s:if>
		<selse><s:text name="ReportPolicyList.TooMany" /></selse>
	</div>
		
</s:if>
</pics:permission>

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<s:form id="policyList" method="post" cssClass="forms">
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" ><s:text name="global.Contractor" /></a></th>
   		<s:if test="filter.primaryInformation">
			<td><s:text name="global.Contact" /></td>
		</s:if>
	    <td><a><s:text name="ReportPolicyList.PolicyType" /></a></td>
		<s:if test="permissions.operator || permissions.corporate">
			<td><s:text name="global.Status" /></td>
		</s:if>
	    <td><a href="javascript: changeOrderBy('form1','ca.creationDate DESC');" ><s:text name="ReportPolicyList.Effective" /></a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" ><s:text name="ReportPolicyList.Expiration" /></a></td>
	    <s:if test="permissions.operator || permissions.corporate">
	        <td><s:text name="ReportPolicyList.Files" /></td>
	   	</s:if>
	   	<td><s:text name="AmBest" /></td> 
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
			<s:if test="filter.primaryInformation">
				<td>
					<s:property value="get('contactname')"/> <br />
					<s:property value="get('contactphone')"/> <br />
					<a href="mailto:<s:property value="get('contactemail')"/>"><s:property value="get('contactemail')"/></a> <br />
				</td>
			</s:if>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text name="%{get('atype.name')}" /> <s:property value="get('auditFor')"/></a></td>
		    <s:if test="permissions.operator || permissions.corporate">
			    <td><s:text name="AuditStatus.%{get('auditStatus')}"/></td>
		    </s:if>
			<td class="center"><s:date name="get('createdDate')" format="%{getText('date.short')}" /></td>
			<td class="center"><s:date name="get('expiresDate')" format="%{getText('date.short')}" /></td>
		    <s:if test="permissions.operator || permissions.corporate">
				<td class="center">
					<s:if test="get('certID') != null">
						<a href="CertificateUpload.action?id=<s:property value="get('id')"/>&certID=<s:property value="get('certID')"/>&button=download"
							target="_BLANK"><img src="images/icon_insurance.gif" /></a>	
					</s:if>
					<s:else></s:else>
				</td>
			</s:if>
		   	<td>
				<s:iterator value="getDataForAudit(get('auditID'),'AMBest')">
					<s:property value="getAMBestRatings(comment)" escape="false"/>
				</s:iterator>
		   	</td> 
		</tr>
	</s:iterator>
</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
