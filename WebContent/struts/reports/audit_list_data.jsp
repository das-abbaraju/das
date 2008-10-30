<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="report.allRows == 0">
	<div id="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportAuditList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <td><a href="javascript: changeOrderBy('form1','atype.auditName');" >Type</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.createdDate DESC');" >Created</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');" >Submitted</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.closedDate DESC');" >Closed</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" >Expired</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');" >Status</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.percentComplete');" >Comp%</a></td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
	    <td><a href="javascript: changeOrderBy('form1','auditor.name');" >Auditor</a></td>
	    </s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
		<td class="center"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('completedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('closedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('expiresDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td class="right"><s:property value="[0].get('percentComplete')"/>%</td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
		<td><s:property value="[0].get('auditor_name')"/></td>
		</s:if>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
