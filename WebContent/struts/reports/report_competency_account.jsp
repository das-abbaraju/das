<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>HSE Competency By Account Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>
<script type="text/javascript">
$(function() {
	$.mask.definitions['S']='[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
});
</script>
</head>
<body>
<h1>HSE Competency By Account Report</h1>
<s:include value="filters_employee.jsp" />
<s:if test="data.size > 0">
<div class="right">
	<a class="excel" <s:if test="data.size > 500">onclick="return confirm('Are you sure you want to download all
		<s:property value="data.size" /> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportCompetencyByAccount');"
		title="Download all <s:property value="data.size"/> results to a CSV file">Download</a>
</div>

<table class="report">
	<thead>
		<tr>
			<th><a href="?orderBy=name">Company</a></th>
			<th># of Employees</th>
			<th># of Job Roles</th>
			<th><a href="?orderBy=ca99date">Job Role Self Assessment</a></th>
			<th><a href="?orderBy=ca100date">HSE Competency Review</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="d">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('name')" /></a></td>
				<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="@java.net.URLEncoder@encode(#d.get('name'))" />"><s:property value="#d.get('eCount')" /></a></td>
				<td class="right"><a href="JobCompetencyMatrix.action?id=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('jCount')"/></a></td>
				<td>
					<s:if test="#d.get('ca99status').toString().length() > 0">
						<a href="Audit.action?auditID=<s:property value="#d.get('ca99ID')" />">
							<s:property value="#d.get('ca99status')" />
						</a>
					</s:if>
					<s:else>
						<s:property value="#d.get('ca99status')" />
					</s:else>
				</td>
				<td>
					<s:if test="#d.get('ca100status').toString().length() > 0">
						<a href="Audit.action?auditID=<s:property value="#d.get('ca100ID')" />">
							<s:property value="#d.get('ca100status')" />
						</a>
					</s:if>
					<s:else>
						<s:property value="#d.get('ca100status')" />
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

</s:if>
<s:else>
	<div class="info">
		No Records Found.
	</div>
</s:else>
</body>
</html>
