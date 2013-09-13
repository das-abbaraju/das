<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<h3>
	<s:text name="ProfileEdit.label.SwitchAccounts" />
</h3>

<table class="report">
	<thead>
		<tr>
			<td><s:text name="global.Account" /></td>
			<td><s:text name="ProfileEdit.header.UserRole" /></td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>				
				<s:if test="u.id.size() > 1">
					<a href="Login.action?button=switch&switchToUser=<s:property value="u.id" />"><s:property value="u.account.name" /></a>
				</s:if>
				<s:if test="account.users.size() > 1">
					<a href="Login.action?button=switch&switchToUser=<s:property value="user.id" />"><s:property value="user.account.name" /></a>
				</s:if>
			</td>
			<td><s:text name="ProfileEdit.header.Primary" /></td>
		</tr>
		
		<s:iterator value="switchTos">
		<tr>
			<td>
				<a href="Login.action?button=switch&switchToUser=<s:property value="switchTo.id" />"><s:property value="switchTo.account.name" /></a>
			</td>
			<td><s:property value="switchTo.name" /></td>
		</tr>
		</s:iterator>
	</tbody>
</table>