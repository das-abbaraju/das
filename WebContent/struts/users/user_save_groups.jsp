<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
<thead>
	<tr>
		<th colspan="3">Group(s)</th>
	</tr>
</thead>
<tbody>
<s:sort comparator="groupNameComparator" source="user.groups">
<s:iterator>
	<tr>
		<td><s:property value="group.account.name"/></td>
		<td><a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="group.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="group.name"/></a></td>
		<td><a class="remove" href="#" onclick="removeGroup(<s:property value="userGroupID"/>); return false;">remove</a></td>
	</tr>
</s:iterator>
</s:sort>
<s:iterator value="addableGroups">
	<tr>
		<td><s:property value="account.name"/></td>
		<td><a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>" style="font-style: italic; color: red;"><s:property value="name"/></a></td>
		<td><a class="edit" href="#" style="font-style: italic; color: red;" 
			onclick="addGroup(<s:property value="id"/>); return false;">add</a></td>
	</tr>
</s:iterator>
</tbody>
</table>
