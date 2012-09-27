<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td><a href="?orderBy=a.name ASC">Contractor</a></td>
			<td>Document Type</td>
			<td><a href="?orderBy=auditor.name ASC">Auditor</a></td>
			<td>Scheduled Date</td>
			<td>Audit Location</td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text name="%{get('atype.name')}" /> <s:property value="get('auditFor')"/></a></td>
			<td><s:property value="get('auditor_name')"/></td>
			<td><s:date name="get('scheduledDate')" format="%{@com.picsauditing.util.PicsDateFormat@Datetime12Hour}" /></td>
			<td><s:property value="get('auditLocation')" />
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
