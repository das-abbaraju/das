<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>

<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('UserList');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Account Name</td>
		<td>Contact Name</td>
		<td><s:text name="User.phone" /></td>
		<td><s:text name="User.email" /></td>
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
			<td><s:if test="permissions.corporate && permissions.accountId != get('accountID')">
				<s:property value="get('companyName')" />
			</s:if>
				<s:else>
				<a href="UsersManage.action?account=<s:property value="get('accountID')"/>" class="account<s:property value="get('companyStatus')" />">
						<s:property value="get('companyName')" /></a>
				</s:else>
			</td>
			<td><s:if test="permissions.corporate && permissions.accountId != get('accountID')">
					<s:property value="get('name')" />
				</s:if>
				<s:else>
					<a href="UsersManage.action?account=<s:property value="get('accountID')"/>&user=<s:property value="get('id')"/>">
						<s:property value="get('name')" /></a>
				</s:else>
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
