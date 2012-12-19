<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="tab_permissions" style="display: none;">
	<table style="width: 100%">
		<tr>
			<td>
				<h3>
					<s:text name="ProfileEdit.label.Permissions" />
				</h3>

				<table class="report">
					<thead>
						<tr>
							<th><s:text name="ProfileEdit.header.PermissionName" /></th>
							<th><s:text name="OpType.View" /></th>
							<th><s:text name="OpType.Edit" /></th>
							<th><s:text name="OpType.Delete" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="permissions.permissions">
							<tr>
								<td title="<s:property value="opPerm.helpText" />"><s:property
										value="opPerm.description" /></td>
								<td><s:if test="viewFlag">
										<s:text name="OpType.View" />
									</s:if></td>
								<td><s:if test="editFlag">
										<s:text name="OpType.Edit" />
									</s:if></td>
								<td><s:if test="deleteFlag">
										<s:text name="OpType.Delete" />
									</s:if></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</td>
			<td>
				<h3>
					<s:text name="ProfileEdit.header.VisibleAuditAndPolicyTypes" />
				</h3>

				<div>
					<s:if test="permissions.operatorCorporate">
						<ul>
							<s:iterator value="viewableAuditsList">
								<li><s:property value="name" /></li>
							</s:iterator>
						</ul>
					</s:if>
					<s:else>
						<s:text name="ProfileEdit.message.YouAreAPICSEmployee" />
					</s:else>
				</div> <s:if
					test="permissions.admin && permissions.shadowedUserID != permissions.userId">
					<h3>
						<s:text name="ProfileEdit.header.Shadowing" />
					</h3>

					<div>
						<s:text name="ProfileEdit.message.YouAreCurrentlyShadowing">
							<s:param value="%{permissions.shadowedUserName}" />
						</s:text>
					</div>
				</s:if>
			</td>
		</tr>
	</table>
	<table>
		<tr>
			<td>
				<h3>
					<s:text name="UsersManage.Groups" />
				</h3>

				<table class="report">
					<thead>
						<tr>
							<th><s:text name="ProfileEdit.label.MemberOf" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="allInheritedGroups">
							<tr>
								<td><s:property value="name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</td>
		</tr>
	</table>
</div>
