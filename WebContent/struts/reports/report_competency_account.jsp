<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js?v=${version}"></script>
<script type="text/javascript">
$(function() {
	$.mask.definitions['S']='[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
	$('table.report a.add').live('click', function() {
		$(this).closest('td').html('<img src="images/ajax_process.gif" />')
	});
});
</script>
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>

<s:include value="../actionMessages.jsp" />

<s:include value="filters_employee.jsp" />
<s:if test="data.size > 0">
	<div class="right">
		<a class="excel" <s:if test="data.size > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{data.size}" /></s:text>');"</s:if> 
			href="javascript: download('ReportCompetencyByAccount');"
			title="<s:text name="javascript.DownloadAllRows"><s:param value="%{data.size}" /></s:text>"><s:text name="global.Download" /></a>
	</div>
	<s:set name="showAddMessage" value="false" />
	<table class="report">
		<thead>
			<tr>
				<th><a href="?orderBy=name"><s:text name="global.Company" /></a></th>
				<th><s:text name="button.Add" /></th>
				<th><s:text name="%{scope}.label.NumberOfEmployees" /></th>
				<th><s:text name="%{scope}.label.NumberOfJobRoles" /></th>
				<th><a href="?orderBy=ca99date"><s:text name="AuditType.99.name" /></a></th>
				<th><a href="?orderBy=ca100date"><s:text name="AuditType.100.name" /></a></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat" id="d">
				<tr>
					<td>
						<s:if test="#d.get('worksFor') == 1">
							<a href="ContractorView.action?id=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('name')" /></a>
						</s:if>
						<s:else>
							<s:property value="#d.get('name')" />
						</s:else>
					</td>
					<td class="center">
						<s:if test="#d.get('worksFor') == 0">
							<s:set name="showAddMessage" value="true" />
							<a href="<s:property value="scope" />!add.action?operator=<s:property value="permissions.accountId" />&contractor=<s:property value="#d.get('accountID')" />" class="add"></a>
						</s:if>
					</td>
					<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="@java.net.URLEncoder@encode(#d.get('name'))" />"><s:property value="#d.get('eCount')" /></a></td>
					<td class="right"><a href="JobCompetencyMatrix.action?account=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('jCount')"/></a></td>
					<td>
						<s:if test="#d.get('worksFor') == 1">
							<a href="Audit.action?auditID=<s:property value="#d.get('ca99ID')" />">
								<s:property value="#d.get('ca99status')" />
							</a>
						</s:if>
						<s:else>
							<s:property value="#d.get('ca99status')" />
						</s:else>
					</td>
					<td>
						<s:if test="#d.get('worksFor') == 1">
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
	<s:if test="#showAddMessage">
		<div class="info"><s:text name="%{scope}.help.AddCompany" /></div>
	</s:if>
</s:if>
<s:else>
	<div class="info">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:else>
</body>
</html>
