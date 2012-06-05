<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:text name="UsersManage.title" />
		
		<s:if test="user.id > 0">
			: <s:property value="user.name" />
		</s:if>
	</title>

	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/users_manage.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp" />
	
	<script type="text/javascript" src="js/user_manage.js?v=<s:property value="version"/>"></script>

	<script type="text/javascript">
		var accountID = '<s:property value="account.id" />';
		var currentUserID = 0;
		
		<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>
		
		var permTypes = new Array();
		
		<s:iterator value="permissions.permissions">
			<s:if test="grantFlag == true">permTypes['<s:property value="opPerm"/>'] = new Array("<s:property value="opPerm.helpText"/>",<s:property value="opPerm.usesView()"/>,<s:property value="opPerm.usesEdit()"/>,<s:property value="opPerm.usesDelete()"/>);</s:if>
		</s:iterator>
		
		$(function () {
			$('#accountMoveSuggest').autocomplete('UsersManageAjax.action?user=<s:property value="user.id"/>&button=Suggest').result(function (event, data) {
				$('#moveToAccount').val(data[1]);
			});
			
			$('#departmentSuggest').autocomplete('UsersManageAjax.action?user=<s:property value="user.id"/>&button=Department').result(function (event, data) {
				$('#departmentRole').val(data[3]);
			});
		});
	</script>
</head>
<body>
	<h1>
		<s:text name="UsersManage.title" />
	</h1>

	<s:if test="account.contractor">
		<a href="ContractorView.action?id=<s:property value="account.id"/>"><s:property
				value="account.name" /></a>
	</s:if>

	<s:if test="account.operatorCorporate">
		<a href="FacilitiesEdit.action?operator=<s:property value="account.id"/>">
			<s:property value="account.name" />
		</a>
	</s:if>

	<s:if test="account.assessment">
		<a href="AssessmentCenterEdit.action?center=<s:property value="account.id"/>">
			<s:property value="account.name" />
		</a>
	</s:if>

	<s:if test="account.admin">PICS</s:if>

	&gt;
		<a href="UsersManage.action?account=<s:property value="account.id"/>">
			<s:text	name="UsersManage.title" />
		</a>

	<s:if test="user.id > 0">
		&gt; <a href="?user=<s:property value="user.id"/>">
				<s:property value="user.name" />
			</a>
	</s:if>

	<s:if test="user.id == 0">
		&gt; <s:text name="UsersManage.NewUser" />
	</s:if>

	<div id="manage_controls"
		<s:if test="user != null">style="display:none"</s:if>>
		<s:if test="!account.contractor">
			<div id="search">
				<s:form id="form1" method="get">
					<input type="hidden" name="button" value="Search">
					<button class="picsbutton positive" type="submit">
						<s:text name="button.Search" />
					</button>
					<br />

					<div class="filterOption">
						<h4>
							<s:text name="UsersManage.Type" />
							:
						</h4>

						<s:hidden name="account.id" value="%{account.id}" />

						<s:radio name="isGroup"
							list="#{'Yes':getTextNullSafe('UsersManage.Groups'), 'No':getTextNullSafe('global.Users'), '':getTextNullSafe('UsersManage.Both')}"
							value="isGroup" theme="pics" cssClass="inline" />
					</div>

					<div class="filterOption">
						<h4>
							<s:text name="global.Status" />
							:
						</h4>

						<s:radio name="isActive"
							list="#{'Yes':getTextNullSafe('global.Active'), 'No':getTextNullSafe('UsersManage.Inactive'), '':getTextNullSafe('JS.Filters.status.All')}"
							value="isActive" theme="pics" cssClass="inline" />
					</div>
				</s:form>

				<div class="clear"></div>
			</div>
		</s:if>

		<div style="margin: 5px 0 5px 0; list-style: none;">
			<s:if test="!account.contractor">
				<s:url var="add_group" action="UsersManage" method="add">
					<s:param name="account" value="%{account.id}" />
					<s:param name="isActive" value="%{isActive}" />
					<s:param name="isGroup" value="%{isGroup}" />
					<s:param name="userIsGroup" value="'Yes'" />
				</s:url>
				<a href="${add_group}" class="add" id="add_group"> <s:text
						name="UsersManage.addGroup" />
				</a>
			</s:if>
			<s:url var="add_user" action="UsersManage" method="add">
				<s:param name="account" value="%{account.id}" />
				<s:param name="isActive" value="%{isActive}" />
				<s:param name="isGroup" value="%{isGroup}" />
				<s:param name="userIsGroup" value="'No'" />
			</s:url>
			<a href="${add_user}" class="add" id="add_user"> <s:text
					name="UsersManage.addUser" />
			</a>

			<s:if test="!account.contractor">
				<a class="preview"
					href="ReportUserPermissionMatrix.action?accountID=<s:property value="account.id"/>">
					<s:text name="ReportUserPermissionMatrix.title" />
				</a>
			</s:if>

			<s:if test="account.operatorCorporate">
				<a class="preview"
					href="ReportEmailSubscriptionMatrix.action?account=<s:property value="account.id"/>">
					<s:text name="ReportEmailSubscriptionMatrix.title" />
				</a>
			</s:if>

			<s:if test="account.contractor && account.users.size() > 1">
				<a class="edit"
					href="ManageUserPermissions.action?id=<s:property value="account.id"/>">
					<s:text name="ManageUserPermissions.title" />
				</a>
			</s:if>
		</div>
		<table>
			<tr>
				<td>
					<table class="report">
						<thead>
							<tr>
								<td>&nbsp;</td>
								<td colspan="2"><s:text name="UsersManage.UserGroup" /></td>
								<td><s:text name="User.lastLogin" /></td>
							</tr>
						</thead>

						<s:iterator value="userList" status="stat">
							<tr>
								<td class="right"><s:property value="#stat.count" />.</td>

								<s:if test="get('isGroup') == 'Yes'">
									<td><s:text name="UsersManage.Group" /></td>
									<td style="font-weight: bold"><a
										href="?account=<s:property value="get('accountID')"/>&user=<s:property value="get('id')"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>">
											<s:property value="get('name')" />
									</a></td>
									<td><s:text name="global.NA" /></td>

								</s:if>
								<s:else>
									<td><s:text name="UsersManage.User" /></td>
									<td><a
										href="?account=<s:property value="get('accountID')"/>&user=<s:property value="get('id')"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"
										class="userActive<s:property value="get('isActive')" />">
											<s:property value="get('name')" />
									</a></td>
									<td><s:if test="get('lastLogin') != null">
											<s:date name="get('lastLogin')" />
										</s:if> <s:else>
											<s:text name="UsersManage.never" />
										</s:else></td>
								</s:else>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</div>

	<div id="user_edit">
		<s:include value="../actionMessages.jsp" />
		<fieldset class="form submit">
		<s:if test="user != null">
			<ul class="manage-users-actions">
				<li>
					<a class="btn" href="javascript:;" onclick="showUserList();">
						<s:text name="UsersManage.BackToUserList" />
					</a>
				</li>
				
				<s:if test="!user.group  && user.id>0">
					<li>
						<a class="btn" href="ChangePassword.action?source=manage&user=<s:property value="user.id"/>">
							<s:text name="button.password" />
						</a>
					</li>
					<li>
						<s:if test="hasProfileEdit">
							<a class="btn" href="UsersManage!emailPassword.action?user=<s:property value="user.id"/>">
								<s:text name="UsersManage.SendResetPasswordEmail" />
							</a>
						</s:if>
					</li>
					 <pics:permission perm="SwitchUser">
						<li>
							<a class="btn" href="Login.action?button=login&switchToUser=<s:property value="user.id"/>">
								<s:text name="UsersManage.SwitchToThisUser" />
							</a>
						</li>
						
						<s:if test="isBetaEnvironment()">
							<li>
								<a class="btn" href="http://stable.picsorganizer.com/Login.action?button=login&switchToUser=<s:property value="user.id"/>">
									<s:text name="UsersManage.SwitchToThisUserStable" />
								</a>
							</li>
						</s:if>
						
						<s:if test="!isLiveEnvironment()">
							<pics:toggle name="SwitchUserServer">
								<a class="btn" href="UsersManage!switchUserToDifferentServer.action?user=<s:property value="user.id"/>" >
									<s:text name="UsersManage.SwitchToThisUserStable" />
								</a>
							</pics:toggle>
						</s:if>
					</pics:permission>
				</s:if>
			</ul>
		</fieldset>
	</div>

			<s:form id="UserSave">
				<s:if test="user.locked">
					<div class="alert">
						<s:text name="UsersManage.AccountLocked" />
						<s:if test="permissions.admin">
							<span title="<s:text name="UsersManage.PressButtonToUnlock" />">
								<s:submit method="unlock" cssClass="picsbutton negative"
									value="%{getText('UsersManage.UnlockThisAccount')}" />
							</span>
						</s:if>
					</div>
				</s:if>

				<s:hidden name="user" />
				<s:hidden name="account" />
				<s:hidden name="isGroup" />
				<s:hidden name="isActive" />
				<s:hidden name="userIsGroup" />

				<fieldset class="form">
					<h2 class="formLegend">
						<s:text name="UsersManage.UserGroupDetails">
							<s:param value="%{user.group ? 1 : 0}" />
						</s:text>
					</h2>

					<ol>
						<s:if test="account.users.size() > 1">
							<s:if test="user.id > 0">
								<li><label> <s:text
											name="UsersManage.UserGroupNumber">
											<s:param value="%{user.group ? 1 : 0}" />
										</s:text>:
								</label> <s:property value="user.id" /></li>
								<li><label> <s:text name="UsersManage.DateCreated" />
								</label> <s:date name="user.creationDate"
										format="%{getText('date.short')}" /></li>
							</s:if>
						</s:if>

						<li><label> <s:text name="UsersManage.DisplayName" />
						</label> <s:if test="user.isGroup.toString() == 'No'">
								<s:textfield name="user.name" size="30" />
							</s:if> <s:else>
								<s:textfield name="user.name" size="30"
									onchange="checkGroupName(this.value);" />
								<span id="groupname_status"></span>
							</s:else></li>

						<s:if test="user.isGroup.toString() == 'No'">
							<li><s:textfield id="departmentSuggest"
									name="user.department" size="15" theme="formhelp" /></li>

							<li><s:textfield name="user.email" size="40" theme="form" />
							</li>
							<li>
								<s:textfield name="user.username" size="30" onchange="checkUsername(this.value);" theme="form" /> 
								<span id="username_status"></span>
							</li>

							<s:if test="user.id == 0">
								<li><label> <s:text
											name="UsersManage.SendActivationEmail" />
								</label> <s:checkbox id="sendActivationEmail" name="sendActivationEmail" />
								</li>
							</s:if>


							<li><s:textfield name="user.phone" size="15" theme="form" />
							</li>
							<li><s:textfield name="user.fax" size="15" theme="form" />
							</li>

							<s:if test="i18nReady">
								<li><s:select
										list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
										name="user.locale"
										listValue="@org.apache.commons.lang.StringUtils@capitalize(getDisplayName(language))"
										theme="form" /></li>
							</s:if>

							<li><s:select name="user.timezone" value="user.timezone.iD"
									theme="form"
									list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES" /></li>

							<s:if test="user.account.id != 1100">
								<li><label><s:text name="global.ContactPrimary" />:</label>
									<s:checkbox id="setPrimaryAccount" name="setPrimaryAccount" />

									<pics:fieldhelp title="Primary Contact">
										<p>
											<s:text name="UsersManage.SetUserPrimaryContact" />
										</p>
									</pics:fieldhelp></li>
							</s:if>

							<s:if test="account.contractor">
								<li><label> <s:text name="UsersManage.UserRole" />
								</label> <s:checkbox id="conAdmin" name="conAdmin" /> <label
									for="conAdmin" class="checkbox"> <b><s:text
												name="OpPerms.ContractorAdmin.description" /></b> <i>(<s:text
												name="OpPerms.ContractorAdmin.helpText" />)
									</i>
								</label></li>
								<li><s:checkbox id="conBilling" name="conBilling" /> <label
									for="conBilling" class="checkbox"> <b><s:text
												name="OpPerms.ContractorBilling.description" /></b> <i> (<s:text
												name="OpPerms.ContractorBilling.helpText" />)
									</i>
								</label></li>
								<li><s:checkbox id="conSafety" name="conSafety" /> <label
									for="conSafety" class="checkbox"> <b><s:text
												name="OpPerms.ContractorSafety.description" /></b> <i> (<s:text
												name="OpPerms.ContractorSafety.helpText" />)
									</i>
								</label></li>
								<li><s:checkbox id="conInsurance" name="conInsurance" /> <label
									for="conInsurance" class="checkbox"> <b><s:text
												name="OpPerms.ContractorInsurance.description" /></b> <i> (<s:text
												name="OpPerms.ContractorInsurance.helpText" />)
									</i>
								</label></li>
							</s:if>

							<s:if test="user.id > 0">
								<li><label> <s:text name="User.lastLogin" />
								</label> <s:if test="user.lastLogin != null">
										<s:date name="user.lastLogin" />
									</s:if> <s:else>
										<s:text name="UsersManage.never" />
									</s:else></li>
							</s:if>

							<!-- CSR Shadowing -->
							<s:if test="csr && permissions.admin">
								<li><label> <s:text name="UsersManage.ShadowCSR" />
								</label> <s:select list="csrs" listKey="user.id" listValue="user.name"
										headerKey="0"
										headerValue="- %{getTextNullSafe('UsersManage.SelectCSR')} -"
										name="shadowID"
										value="%{user.shadowedUser != null ? user.shadowedUser.id : 0}" />
								</li>
							</s:if>
						</s:if>

						<s:if test="user.id > 0">
							<s:if test="permissions.isAdmin()">
								<!-- Move User to Account -->
								<s:hidden name="moveToAccount" id="moveToAccount" />

								<li><label> <s:text
											name="UsersManage.MoveUserToAccount" />
								</label> <s:textfield id="accountMoveSuggest" /><br /> <pics:fieldhelp
										title="Move User to Account">
										<p>
											<s:text name="UsersManage.MoveUserToAccount.help" />
										</p>
									</pics:fieldhelp> <s:submit method="move" cssClass="picsbutton"
										value="%{getText('UsersManage.button.MoveUser')}"
										onclick="return confirm('%{getText('UsersManage.confirm.Move')}');" />
								</li>
							</s:if>


						</s:if>
						<s:else>
							<s:hidden name="user.isActive" value="Yes" />
						</s:else>

						<li><s:if
								test="user.id == 0 && (permissions.picsEmployee || permissions.operatorCorporate)">
								<div class="alert">
									<s:text name="UsersManage.AssignUserToGroupReminder" />
								</div>
							</s:if></li>
					</ol>
				</fieldset>

				<fieldset class="form submit">

					<s:if test="user.activeB">
						<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
						<pics:permission perm="EditUsers" type="Edit">							
							<s:if test="user.id > 0">
								<s:if test="!setPrimaryAccount">
									<s:submit method="inActivate" cssClass="picsbutton negative" value="%{getText('button.Inactivate')}" onclick="return confirm('%{getText('UsersManage.confirm.Inactivate')}');" />
								</s:if>
							</s:if>
						</pics:permission>
					</s:if>
					<s:if test="!user.activeB">
						<s:submit method="activate" cssClass="picsbutton positive" value="%{getText('button.Activate')}" />
						<pics:permission perm="EditUsers" type="Delete">							
							<s:if test="user.id > 0">
								<s:submit method="delete" cssClass="picsbutton negative" value="%{getText('button.Delete')}" onclick="return confirm('%{getText('UsersManage.confirm.Delete')}');" />
							</s:if>
						</pics:permission>
					</s:if>
				</fieldset>
			</s:form>

			<br clear="all">

			<s:if test="user.id > 0">
				<s:if test="!account.contractor">
					<s:if test="!user.superUser">
						<div id="permissionReport" style="width: 600px">
							<s:include value="user_save_permissions.jsp" />
						</div>

						<div id="groupReport">
							<s:include value="user_save_groups.jsp" />
						</div>
					</s:if>

					<s:if test="user.group">
						<div id="memberReport">
							<s:include value="user_save_members.jsp" />
						</div>
					</s:if>
				</s:if>
				<!-- LW: Estevan specified everyone should be able to see user_switch_accts page. -->
				<s:include value="user_switch_accts.jsp" />


				<s:if test="permissions.admin">
					<s:if test="user.group">
						<div id="userSwitch">
							<s:include value="user_save_userswitch.jsp" />
						</div>
					</s:if>
				</s:if>

				<s:if test="!user.group">
					<table class="report">
						<thead>
							<tr>
								<th><s:text name="Login.LoginDate" /></th>
								<th><s:text name="Login.IPAddress" /></th>
								<s:if test="permissions.isDeveloperEnvironment()">
									<th><s:text name="Login.Server" /></th>
								</s:if>
								<th><s:text name="global.Browser" /></th>
								<th><s:text name="global.Notes" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="recentLogins">
								<tr>
									<td><s:date name="loginDate" /></td>
									<td><a
										href="http://www.hostip.info/?spip=<s:property value="remoteAddress" />">
											<s:property value="remoteAddress" />
									</a></td>
									<td>
										<s:if test="permissions.isDeveloperEnvironment()">
											<s:property value="serverAddress" />
										</s:if>
									</td>
									<td>
										<s:property value="browser" />
									</td>
									<td><s:if test="admin.id > 0">
											<s:text name="Login.LoginBy">
												<s:param value="%{admin.name}" />
												<s:param value="%{admin.account.name}" />
											</s:text>
										</s:if> <s:if test="successful == 'N'">
											<s:text name="ProfileEdit.message.IncorrectPasswordAttempt" />
										</s:if></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
			</s:if>
		</s:if>
	</div>
</body>
