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
					<s:set name="tempList" value="#{'':'', 'true':'Grant', 'false':'Revoke'}"/>
					<td><s:if test="opPerm.usesView()"><s:select list="#attr.tempList" name="viewFlag" onchange="javascript: return updatePermission('%{id}','View',this);"/></s:if></td>
					<td><s:if test="opPerm.usesEdit()"><s:select list="#attr.tempList" name="editFlag" onchange="javascript: return updatePermission('%{id}','Edit',this);"/></s:if></td>
					<td><s:if test="opPerm.usesDelete()"><s:select list="#attr.tempList" name="deleteFlag" onchange="javascript: return updatePermission('%{id}','Delete',this);"/></s:if></td>
					<td><s:select list="#attr.tempList" name="grantFlag" onchange="javascript: return updatePermission('%{id}','Grant',this);"/></td>
					<td>
						<div class="buttons">
						<button name="button"
							onclick="removePermission(<s:property value="id"/>);">Remove</button>
						</div>
					</td>
				</pics:permission>
				<pics:permission perm="EditUsers" type="Grant" negativeCheck="true">
					<td><s:property value="viewFlag ? 'Grant' : 'Revoke'"/></td>
					<td><s:property value="editFlag ? 'Grant' : 'Revoke'"/></td>
					<td><s:property value="deleteFlag ? 'Grant' : 'Revoke'"/></td>
					<td><s:property value="grantFlag ? 'Grant' : 'Revoke'"/></td>
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
