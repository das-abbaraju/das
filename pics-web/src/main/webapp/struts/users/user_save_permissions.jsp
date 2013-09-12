<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />
<table class="report" style="width: 100%" width="100%">
	<thead>
		<tr>
			<th><s:text name="OpPerms" /></th>
			<th><s:text name="OpType.View" /></th>
			<th><s:text name="OpType.Edit" /></th>
			<th><s:text name="OpType.Delete" /></th>
			<th><s:text name="OpType.Grant" /></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="user.ownedPermissions">
			<tr id="permission_<s:property value="id"/>">
				<s:set var="changed_by">
					<s:text name="UsersManage.ChangedBy">
						<s:param value="lastUpdate" />
						<s:param value="grantedBy.name" />
					</s:text>
				</s:set>
				<td rowspan="2" style="font-weight: bold; cursor: help" title="${changed_by}">
					<s:text name="%{opPerm.getI18nKey('description')}" />
				</td>

				<pics:permission perm="EditUsers" type="Grant">
					<s:set name="tempList" value="#{'':'', 'true':getTextNullSafe('OpType.Grant'), 'false':getTextNullSafe('OpType.Revoke')}"/>
					<td>
						<s:if test="opPerm.usesView()">
							<s:select
								list="#attr.tempList"
								name="viewFlag"
								onchange="javascript: return updatePermission('%{id}','View',this);"
							/>
						</s:if>
					</td>
					<td>
						<s:if test="opPerm.usesEdit()">
							<s:select
								list="#attr.tempList"
								name="editFlag"
								onchange="javascript: return updatePermission('%{id}','Edit',this);"
							/>
						</s:if>
					</td>
					<td>
						<s:if test="opPerm.usesDelete()">
							<s:select
								list="#attr.tempList"
								name="deleteFlag"
								onchange="javascript: return updatePermission('%{id}','Delete',this);"
							/>
						</s:if>
					</td>
					<td>
						<s:select
							list="#attr.tempList"
							name="grantFlag"
							onchange="javascript: return updatePermission('%{id}','Grant',this);"
						/>
					</td>
					<td>
						<a href="#" class="remove" onclick="removePermission(<s:property value="id"/>); return false;">
							<s:text name="button.Remove" />
						</a>
					</td>
				</pics:permission>
				<pics:permission perm="EditUsers" type="Grant" negativeCheck="true">
					<td><s:property value="viewFlag ? getTextNullSafe('OpType.Granted') : getTextNullSafe('OpType.Revoked')" /></td>
					<td><s:property value="editFlag ? getTextNullSafe('OpType.Granted') : getTextNullSafe('OpType.Revoked')" /></td>
					<td><s:property value="deleteFlag ? getTextNullSafe('OpType.Granted') : getTextNullSafe('OpType.Revoked')" /></td>
					<td></td>
					<td></td>
				</pics:permission>
			</tr>
			<tr>
				<td colspan="5"><s:text name="%{opPerm.getI18nKey('helpText')}"/></td>
			</tr>
		</s:iterator>
	<pics:permission perm="EditUsers" type="Grant">
		<tr>
			<td colspan="6">
			<div class="buttons" id="addPermissionButton">
				<button name="button" onclick="addPermission();" id="permButton">
					<s:text name="button.Add" />
				</button>
			</div>
			<s:select
				id="newPermissionSelect"
				list="grantablePermissions"
				listValue="%{getText(getI18nKey('description'))}"
				name="opPerm"
				headerKey=""
				headerValue="- %{getText('UsersManage.AddPermission')} -" 
				onchange="showPermDesc(this);"
			/>
			</td>
		</tr>
		<tr class="active">
			<td id="permDescription" style="width: 450px;" colspan="6"></td>
		</tr>
	</pics:permission>
	</tbody>
</table>
