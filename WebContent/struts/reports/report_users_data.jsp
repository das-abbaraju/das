<%@ taglib prefix="s" uri="/struts-tags"%>

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
		<td colspan="2">Account Name</td>
		<td>Contact Type</td>
		<td>Contact Name</td>
		<td>Phone</td>
		<td>Email</td>
		<td>Created</td>
		<td>Last Login</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:if test="get('tableType') == 'Acct' || get('AcctType') == 'Contractor'">
					<a href="ContractorView.action?id=<s:property value="get('accountID')"/>">
					<s:property value="get('companyName')" /></a>
				</s:if>
				<s:else>
					<a href="UsersManage.action?accountId=<s:property value="get('accountID')"/>">
					<s:property value="get('companyName')" /></a>	
				</s:else>
			</td>
			<td>
			<s:if test="get('columnType')== 'User' && get('AcctType') == 'Contractor'">
				<s:property value="get('AcctType')"/>	
			</s:if>
			<s:else>
				<s:property value="get('columnType')"/>
			</s:else>
			</td>
			<td><s:if test="get('tableType') == 'Acct' || get('AcctType') == 'Contractor'">
				<s:property value="get('name')" />
				</s:if>
				<s:else>
					<a href="UsersManage.action?accountId=<s:property value="get('accountID')"/>&user.id=<s:property value="get('id')"/>">
					<s:property value="get('name')" /></a>
				</s:else>
			</td>
			<td><s:property value="get('phone')" /></td>
			<td><s:property value="get('email')" /></td>
			<td><s:date name="get('creationDate')" format="MM/dd/yyyy"/></td>
			<td><s:date name="get('lastLogin')" format="MM/dd/yyyy hh:mm" /></td>
		</tr>
	</s:iterator>

</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
