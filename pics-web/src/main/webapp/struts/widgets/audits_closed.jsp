<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="recentlyClosed.size == 0">
<div class="info"><s:text name="global.NoRecordsToDisplay" /></div>
</s:if>
<s:else>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Contractor" /></th>
			<th><s:text name="global.DocumentType" /></th>
			<th><s:text name="AuditStatus.Complete" /></th>
		</tr>
	</thead>
	<s:iterator value="recentlyClosed">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="audit.contractorAccount.id"/>"><s:property value="audit.contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="audit.id"/>"><s:text name="%{audit.auditType.getI18nKey('name')}" /><s:if test="audit.auditFor.length() > 0"> - <s:property value="audit.auditFor"/></s:if>
			<br/><s:if test="permissions.admin"> For <s:property value=""/></s:if>
			</a></td>
			<td class="center"><s:date name="statusChangedDate" /></td>
		</tr>
	</s:iterator>
</table>
</s:else>
