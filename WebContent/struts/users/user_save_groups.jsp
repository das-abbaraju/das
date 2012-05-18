<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report">
	<thead>
		<tr>
			<th colspan="3">
				<s:text name="UserGroupSave.InheritPermissionsFrom" />
			</th>
		</tr>
	</thead>
<tbody>
<s:sort comparator="groupNameComparator" source="user.groups">
	<s:iterator>
		<s:if test="group.group">
			<tr>
				<td>
					<s:property value="group.account.name"/>
				</td>
				<s:if test="permissions.accountId == group.account.id">
					<td>
						<a href="?accountId=<s:property value="group.account.id"/>&user=<s:property value="group.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>">
							<s:property value="group.name"/>
						</a>
					</td>
					<pics:permission perm="EditUsers" type="Edit">
						<td>
							<a class="remove" href="#" onclick="removeGroup(<s:property value="id"/>); return false;">
								<s:text name="button.Remove" />
							</a>
						</td>
					</pics:permission>
				</s:if>
				<s:else>
					<pics:permission perm="AllOperators">
						<td>
							<a href="?accountId=<s:property value="group.account.id"/>&user=<s:property value="group.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>">
								<s:property value="group.name"/>
							</a>
						</td>
						<pics:permission perm="EditUsers" type="Edit">
							<td>
								<a class="remove" href="#" onclick="removeGroup(<s:property value="id"/>); return false;">
									<s:text name="button.Remove" />
								</a>
							</td>
						</pics:permission>
					</pics:permission>
					<pics:permission perm="AllOperators" negativeCheck="true">
						<td><s:property value="group.name"/></td>
						<td>&nbsp;</td>
					</pics:permission>
				</s:else>
			</tr>
		</s:if>
	</s:iterator>
</s:sort>
<s:if test="addableGroups.size > 0">
	<tr id="addGroupButton">
		<td colspan="3">
			<button class="picsbutton" onclick="$('.addableGroup').show(); $('#addGroupButton').hide(); $('#hideGroupButton').show()">
				<s:text name="UserGroupSave.ShowGroupsToAdd" />
			</button>
		</td>
	</tr>
</s:if>
<s:iterator value="addableGroups">
	<tr class="addableGroup">
		<td><s:property value="account.name"/></td>
		<td>
			<a href="?accountId=<s:property value="account.id"/>&user=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>" style="font-style: italic; color: red;">
				<s:property value="name"/>
			</a>
		</td>
		<td>
			<a class="add" href="#" style="font-style: italic; color: red;" onclick="addGroup(<s:property value="id"/>); return false;">
				<s:text name="button.Add" />
			</a>
		</td>
	</tr>
</s:iterator>
<tr class="hideGroupButton" id="hideGroupButton">
		<td colspan="3"><button class="picsbutton" onclick="$('.addableGroup').hide(); $('#hideGroupButton').hide(); $('#addGroupButton').show()">Hide Groups</button></td>
	</tr>
</tbody>
</table>
