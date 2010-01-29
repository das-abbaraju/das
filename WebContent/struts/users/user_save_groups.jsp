<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report">
<thead>
	<tr>
		<th colspan="3">Inherit Permissions From</th>
	</tr>
</thead>
<tbody>
<s:sort comparator="groupNameComparator" source="user.groups">
<s:iterator>
	<tr>
		<td><s:property value="group.account.name"/></td>
		<s:if test="accountId == group.account.id">
			<td><a href="?accountId=<s:property value="group.account.id"/>&user.id=<s:property value="group.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="group.name"/></a></td>
			<pics:permission perm="EditUsers" type="Edit">
				<td><a class="remove" href="#" onclick="removeGroup(<s:property value="id"/>); return false;">remove</a></td>
			</pics:permission>
		</s:if>
		<s:else>
			<pics:permission perm="AllOperators">
				<td><a href="?accountId=<s:property value="group.account.id"/>&user.id=<s:property value="group.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="group.name"/></a></td>
				<pics:permission perm="EditUsers" type="Edit">
					<td><a class="remove" href="#" onclick="removeGroup(<s:property value="id"/>); return false;">remove</a></td>
				</pics:permission>
			</pics:permission>
			<pics:permission perm="AllOperators" negativeCheck="true">
				<td><s:property value="group.name"/></td>
				<td>&nbsp;</td>
			</pics:permission>
		</s:else>
	</tr>
</s:iterator>
</s:sort>
<s:if test="addableGroups.size > 0">
	<tr id="addGroupButton">
		<td colspan="3"><button class="picsbutton" onclick="$('.addableGroup').show(); $('#addGroupButton').hide();">Show Groups to Add</button></td>
	</tr>
</s:if>
<s:iterator value="addableGroups">
	<tr class="addableGroup">
		<td><s:property value="account.name"/></td>
		<td><a href="?accountId=<s:property value="account.id"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>" style="font-style: italic; color: red;"><s:property value="name"/></a></td>
		<td><a class="edit" href="#" style="font-style: italic; color: red;" 
			onclick="addGroup(<s:property value="id"/>); return false;">add</a></td>
	</tr>
</s:iterator>
</tbody>
</table>
