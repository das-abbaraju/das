<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>

<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('UserList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Account Name</td>
		<td>Contact Name</td>
		<td>Phone</td>
		<td>Email</td>
		<td>Created</td>
		<td>Last Login</td>
		<td>Active</td>
		<pics:permission perm="SwitchUser">
			<td></td>
		</pics:permission>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="UsersManage.action?accountId=<s:property value="get('accountID')"/>" class="account<s:property value="get('companyStatus')" />">
					<s:property value="get('companyName')" /></a>
			</td>
			<td><a href="UsersManage.action?accountId=<s:property value="get('accountID')"/>&user.id=<s:property value="get('id')"/>">
					<s:property value="get('name')" /></a>
			</td>
			<td><s:property value="get('phone')" /></td>
			<td><s:property value="get('email')" /></td>
			<td><s:date name="get('creationDate')" format="MM/dd/yyyy"/></td>
			<td><s:date name="get('lastLogin')" format="MM/dd/yyyy hh:mm" /></td>
			<td><s:property value="get('isActive')" /></td>
			<pics:permission perm="SwitchUser">
				<td><a href="Login.action?button=login&switchToUser=<s:property value="get('id')"/>">Switch</a></td>
			</pics:permission>
		</tr>
	</s:iterator>

</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
