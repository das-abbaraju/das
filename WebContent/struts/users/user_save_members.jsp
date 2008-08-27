<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
<thead>
	<tr>
		<th colspan="3">Member(s) of Group</th>
	</tr>
</thead>
<tbody>
<s:iterator value="user.members">
	<tr>
		<td><s:property value="group.account.name"/></td>
		<td><a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="user.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="user.name"/></a></td>
		<td><a href="#" onclick="removeMember(<s:property value="userGroupID"/>); return false;">remove</a></td>
	</tr>
</s:iterator>
<s:iterator value="addableMembers">
	<tr>
		<td><s:property value="account.name"/></td>
		<td><a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>" style="font-style: italic; color: red;"><s:property value="name"/></a></td>
		<td><a href="#" onclick="addMember(<s:property value="id"/>); return false;" 
				style="font-style: italic; color: red;">add</a></td>
	</tr>
</s:iterator>
</tbody>
</table>
