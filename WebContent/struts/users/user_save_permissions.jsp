<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th>Permission</th>
			<th>Read</th>
			<th>Edit</th>
			<th>Delete</th>
			<th>Grant</th>
			<pics:permission perm="EditUsers" type="Grant">
				<th></th>
			</pics:permission>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="user.ownedPermissions">

			<tr id="permission_<s:property value="id"/>">
				<td><s:property value="opPerm.description" /></td>

				<pics:permission perm="EditUsers" type="Grant">
					<td><s:checkbox name="viewFlag" onclick="javascript: return updatePermission('%{id}','View',this);"/></td>
					<td><s:checkbox name="editFlag" onclick="javascript: return updatePermission('%{id}','Edit',this);"/></td>
					<td><s:checkbox name="deleteFlag" onclick="javascript: return updatePermission('%{id}','Delete',this);"/></td>
					<td><s:checkbox name="grantFlag" onclick="javascript: return updatePermission('%{id}','Grant',this);"/></td>
					<td>
						<div class="buttons">
						<button name="button"
							onclick="removePermission(<s:property value="id"/>);">Remove</button>
						</div>
					</td>
				</pics:permission>
				<pics:permission perm="EditUsers" type="Grant" negativeCheck="true">
					<td><s:property value="viewFlag ? 'Y' : 'N'"/></td>
					<td><s:property value="editFlag ? 'Y' : 'N'"/></td>
					<td><s:property value="deleteFlag ? 'Y' : 'N'"/></td>
					<td><s:property value="grantFlag ? 'Y' : 'N'"/></td>
				</pics:permission>
			</tr>
		</s:iterator>
	</tbody>
	<pics:permission perm="EditUsers" type="Grant">
	<tfoot>
		<tr>
			<td colspan="6">
			<div class="buttons" id="addPermissionButton">
				<button name="button" onclick="addPermission();">Add Permission</button>
			</div>
			<s:select id="newPermissionSelect" list="grantablePermissions"
				listValue="description" name="opPerm" 
				onchange="showPermDesc(this);" />
			</td>
		</tr>
		<tr class="active">
			<td id="permDescription" style="width: 450px;" colspan="6"></td>
		</tr>
	</tfoot>
	</pics:permission>
</table>
