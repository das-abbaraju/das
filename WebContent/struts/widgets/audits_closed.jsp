<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Type</th>
		<th>Closed</th>
		</tr>
	</thead>
	<s:iterator value="recentlyClosed">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="audit.contractorAccount.id"/>"><s:property value="audit.contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="audit.id"/>"><s:property value="audit.auditType.auditName"/><s:if test="audit.auditFor.length() > 0"> - <s:property value="audit.auditFor"/></s:if>
			<br/><s:if test="permissions.admin"> For <s:property value=""/></s:if>
			</a></td>
			<td class="center"><s:date name="statusChangedDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
	<s:if test="recentlyClosed.size == 0">
		<tr>
			<td colspan="4" class="center">No currently schedule audits</td>
		</tr>
	</s:if>
</table>
