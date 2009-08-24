<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report">
<thead>
	<tr>
		<th colspan="3">Member(s) of Group</th>
	</tr>
</thead>
<tbody>
<s:sort comparator="userNameComparator" source="user.members">
<s:iterator>
	<tr>
		<td><s:property value="user.account.name"/></td>
		<td><a href="?accountId=<s:property value="user.account.id"/>&user.id=<s:property value="user.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="user.name"/></a></td>
		<pics:permission perm="EditUsers" type="Delete">
			<td><a class="remove" href="#" onclick="removeMember(<s:property value="id"/>); return false;">remove</a></td>
		</pics:permission>
	</tr>
</s:iterator>
</s:sort>
<s:iterator value="addableMembers">
	<tr>
		<td><s:property value="account.name"/></td>
		<td><a href="?accountId=<s:property value="account.id"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>" style="font-style: italic; color: red;"><s:property value="name"/></a></td>
		<td><a class="edit" href="#" onclick="addMember(<s:property value="id"/>); return false;" 
				style="font-style: italic; color: red;">add</a></td>
	</tr>
</s:iterator>
</tbody>
</table>
