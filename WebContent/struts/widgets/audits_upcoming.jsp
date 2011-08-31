<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th><s:text name="global.Contractor" /></th>
		<th><s:text name="global.Type" /></th>
		<th><s:text name="UpcomingAuditsAjax.Created" /></th>
		</tr>
	</thead>
	<s:iterator value="upcoming">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="audit.contractorAccount.id"/>"><s:property value="audit.contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="audit.id"/>"><s:property value="audit.auditType.name"/></a></td>
			<td class="center"><s:date name="creationDate" /></td>
		</tr>
	</s:iterator>
	<s:if test="upcoming.size == 0">
		<tr>
			<td colspan="3" class="center"><s:text name="UpcomingAuditsAjax.NoUpcomingAudits" /></td>
		</tr>
	</s:if>
</table>
