<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<td>Time</td>
			<td>Contact</td>
			<td>Name</td>
		</tr>
	</thead>
	<s:iterator value="loggedOperators">
		<tr>
			<td><nobr><s:property value="formatDate(lastLogin, 'MM/dd hh:mm')" /></nobr></td>
			<td><a href="UsersManage.action?user.id=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td><a href="FacilitiesEdit.action?id=<s:property value="account.id"/>"><s:property value="account.name" /></td>
		</tr>
	</s:iterator>
</table>
