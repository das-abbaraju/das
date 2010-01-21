<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report" style="width: 100%" width="100%">
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

			<tr id="permission_<s:property value="id"/>">
				<td rowspan="2" style="font-weight: bold; cursor: help" title="Changed <s:date name="lastUpdate" /> by <s:property value="grantedBy.name"/>">
					<s:property value="opPerm.description" />
				</td>

				<pics:permission perm="EditUsers" type="Grant">
					<s:set name="tempList" value="#{'':'', 'true':'Grant', 'false':'Revoke'}"/>
					<td><s:if test="opPerm.usesView()"><s:select list="#attr.tempList" name="viewFlag" onchange="javascript: return updatePermission('%{id}','View',this);"/></s:if></td>
					<td><s:if test="opPerm.usesEdit()"><s:select list="#attr.tempList" name="editFlag" onchange="javascript: return updatePermission('%{id}','Edit',this);"/></s:if></td>
					<td><s:if test="opPerm.usesDelete()"><s:select list="#attr.tempList" name="deleteFlag" onchange="javascript: return updatePermission('%{id}','Delete',this);"/></s:if></td>
					<td><s:select list="#attr.tempList" name="grantFlag" onchange="javascript: return updatePermission('%{id}','Grant',this);"/></td>
					<td><a href="#" class="remove" onclick="removePermission(<s:property value="id"/>); return false;">remove</a></td>
				</pics:permission>
				<pics:permission perm="EditUsers" type="Grant" negativeCheck="true">
					<td><s:property value="viewFlag ? 'Granted' : 'Revoked'"/></td>
					<td><s:property value="editFlag ? 'Granted' : 'Revoked'"/></td>
					<td><s:property value="deleteFlag ? 'Granted' : 'Revoked'"/></td>
					<td></td>
					<td></td>
				</pics:permission>
			</tr>
			<tr>
				<td colspan="5"><s:property value="opPerm.helpText"/></td>
			</tr>
		</s:iterator>
	<pics:permission perm="EditUsers" type="Grant">
		<tr>
			<td colspan="6">
			<div class="buttons" id="addPermissionButton">
				<button name="button" onclick="addPermission();" id="permButton">Add</button>
			</div>
			<s:select id="newPermissionSelect" list="grantablePermissions"
				listValue="description" name="opPerm" headerKey="" headerValue="- Add Permission -" 
				onchange="showPermDesc(this);" />
			</td>
		</tr>
		<tr class="active">
			<td id="permDescription" style="width: 450px;" colspan="6"></td>
		</tr>
	</pics:permission>
	</tbody>
</table>
