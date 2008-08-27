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
		<th></th>
	</tr>
</thead>
<tbody>
	<s:iterator value="user.ownedPermissions">
		<tr>
			<td><s:property value="opPerm.description"/></td>
			<td></td>
			<td></td>
			<td></td>
			<td>remove</td>
		</tr>
	</s:iterator>
</tbody>
<tfoot>
		<tr>
			<td colspan="6">
				<div class="buttons">
					<button name="button" onclick="addPermission();">Add Permission</button>
					<s:select id="newPermissionSelect" list="grantablePermissions" listValue="description" name="opPerm" />
				</div>
			</td>
		</tr>
	</tfoot>
</table>
