<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportPolicyList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="policyList" method="post" cssClass="forms">
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
   		<s:if test="filter.primaryInformation">
			<td>Contact</td>
		</s:if>
	    <td><a>Policy Type</a></td>
		<s:if test="permissions.operator || permissions.corporate">
			<td>Status</td>
		</s:if>
	    <td><a href="javascript: changeOrderBy('form1','ca.creationDate DESC');" >Effective</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" >Expiration</a></td>
	    <s:if test="permissions.operator || permissions.corporate">
	        <td>File(s)</td>
	   	</s:if>
	   	<td>AMBest</td> 
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
		    <td><s:property value="get('auditStatus')"/></td>
	    </s:if>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
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
