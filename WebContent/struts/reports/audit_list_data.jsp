<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div id="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportAuditList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <td><a href="javascript: changeOrderBy('form1','atype.auditName');" >Type</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.creationDate DESC');" >Created</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');" >Submitted</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.closedDate DESC');" >Closed</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" >Expired</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');" >Status</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.percentComplete');" >Comp%</a></td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
	    <td><a href="javascript: changeOrderBy('form1','auditor.name');" >Auditor</a></td>
	    </s:if>
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Phone2</td>
			<td>Email</td>
			<td>Office Address</td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
			<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
			<td>Zip</td>
			<td>Second Contact</td>
			<td>Second Phone</td>
			<td>Second Email</td>
			<td>Web_URL</td>
		</s:if>
		<s:if test="showTrade">
			<td>Trade</td>
			<td>Industry</td>			
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/> <s:property value="get('auditFor')"/></a></td>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('completedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('closedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
		<td><s:property value="get('auditStatus')"/></td>
		<td class="right"><s:property value="get('percentComplete')"/>%</td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
		<td><s:property value="get('auditor_name')"/></td>
		</s:if>
		<s:if test="showContact">
			<td><s:property value="get('contact')"/></td>
			<td><s:property value="get('phone')"/></td>
			<td><s:property value="get('phone2')"/></td>
			<td><s:property value="get('email')"/></td>
			<td><s:property value="get('address')"/></td>
			<td><s:property value="get('city')"/></td>
			<td><s:property value="get('state')"/></td>
			<td><s:property value="get('zip')"/></td>
			<td><s:property value="get('secondContact')"/></td>
			<td><s:property value="get('secondPhone')"/></td>
			<td><s:property value="get('secondEmail')"/></td>
			<td><s:property value="get('web_URL')"/></td>
		</s:if>
		<s:if test="showTrade">
			<td><s:property value="get('main_trade')"/></td>
			<td><s:property value="get('industry')"/></td>
		</s:if>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
