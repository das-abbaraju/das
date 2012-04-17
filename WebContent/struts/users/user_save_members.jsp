<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report">
<thead>
	<tr>
		<th colspan="3">
			<s:text name="UsersManage.MembersOfGroup">
				<s:param value="%{user.name}" />
			</s:text>
		</th>
	</tr>
</thead>
<tbody>
<s:sort comparator="userNameComparator" source="user.members">
<s:iterator>
	<tr>
		<td><s:property value="user.account.name"/></td>
		<td style="<s:if test="user.group">font-weight: bold</s:if>">
			<s:url var="go_to_user" action="UsersManage">
				<s:param name="accountId" value="%{user.account.id}" />
				<s:param name="user" value="%{user.id}" />
				<s:param name="isActive" value="%{[1].isActive]" />
				<s:param name="isGroup" value="%{[1].isGroup}" />
			</s:url>
			<a href="${go_to_user}" class="<s:if test="!user.activeB">inactive</s:if>">
				<s:property value="user.name"/>
			</a>
		</td>
		<td>
			<pics:permission perm="EditUsers" type="Edit">
				<a class="remove" href="#" onclick="removeMember(<s:property value="id"/>); return false;">
					<s:text name="button.Remove" />
				</a>
			</pics:permission>
		</td>
	</tr>
</s:iterator>
</s:sort>
<s:if test="addableMembers.size > 0">
	<tr id="addMemberButton">
		<td colspan="3">
			<button class="picsbutton" onclick="$('.addableMember').show(); $('#addMemberButton').hide();">
				<s:text name="UsersManage.ShowMembersToAdd" />
			</button>
		</td>
	</tr>
</s:if>
<s:iterator value="addableMembers">
	<tr class="addableMember">
		<td><s:property value="account.name"/></td>
		<td style="<s:if test="group">font-weight: bold</s:if>">
			<s:url var="go_to_user" action="UsersManage">
				<s:param name="accountId" value="%{user.account.id}" />
				<s:param name="user" value="%{user.id}" />
				<s:param name="isActive" value="%{[1].isActive]" />
				<s:param name="isGroup" value="%{[1].isGroup}" />
			</s:url>
			<a href="${go_to_user}" style="font-style: italic; color: red;">
				<s:property value="name"/>
			</a>
		</td>
		<td>
			<a class="add" href="#" onclick="addMember(<s:property value="id"/>); return false;" style="font-style: italic; color: red;">
				<s:text name="button.Add" />
			</a>
		</td>
	</tr>
</s:iterator>
</tbody>
</table>
