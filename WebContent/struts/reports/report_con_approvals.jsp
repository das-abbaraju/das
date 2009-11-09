<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Approval </title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor Approval</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<s:if test="permissions.operator">
			<td>Date Added</td>
			<td>Work Status</td>
		</s:if>
		<pics:permission perm="ContractorApproval" type="Edit">
			<td></td>
			<td>Notes</td>
			<td></td>
		</pics:permission>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"
				rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick" title="<s:property value="get('name')" />"
				><s:property value="get('name')" /></a>
			</td>
			<s:if test="permissions.operator">
				<td><s:date name="get('dateAdded')" format="M/d/yy"/></td>
				<td><s:property value="getWorkStatusDesc(get('workStatus'))"/></td>
			</s:if>
			<pics:permission perm="ContractorApproval" type="Edit">
				<s:form action="ContractorApproval" method="POST">
					<s:hidden value="%{get('id')}" name="conID"/>
					<td><s:radio list="#{'Y':'Yes<br/>','N':'No<br/>','P':'Pending'}" name="workStatus" theme="pics"/></td>
					<td><s:textarea name="operatorNotes" cols="15" rows="4"/></td>
					<td><input type="submit" class="picsbutton positive" name="button" value="Save"/></td>
				</s:form>
			</pics:permission>	
		</tr>
	</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</s:else>
</div>

</body>
</html>
