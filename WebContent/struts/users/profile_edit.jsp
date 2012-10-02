<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<head>
	<title><s:text name="ProfileEdit.title"></s:text></title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
	<script type="text/javascript" src="js/profile_edit.js?v=<s:property value="version"/>"></script>
	<script type="text/javascript">
		var accountID = '<s:property value="account.id" />';
		var currentUserID = 0;
		
		<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>
	</script>
	
	<s:include value="../jquery.jsp" />
	
	<script type="text/javascript">
		function checkUsername(username) {
			startThinking({div: 'username_status', message: translate('JS.ProfileEdit.message.CheckingAvailabilityOfUsername')});
			var data = {userID: <s:property value="u.id"/>, username: username};
			$('#username_status').load('user_ajax.jsp', data);
		}
	
		function save(subscription, id, timeperiod) {
		    var data = {
	    	    'eu.id': id, 
	    	    'eu.subscription': subscription,
	    	    'addSubscription': $('#add'+subscription).is(':checked'),
	    	    goEmailSub: false
	   	    };
	
			if(timeperiod != null) {
				data['eu.timePeriod']= $(timeperiod).val();
			}
			
		    var divName = 'td'+subscription;
			startThinking({'div':divName});
	
			$('#'+divName).load('UserEmailSubscriptionSaveAjax.action',data,function() {
					$(this).effect('highlight', {color: '#FFFF11'}, 1000)
				}
			);
	
		}
	
		function showTab(tabName) {
			$('#tab_profile').hide();
			$('#link_tab_profile').removeClass('current');
			$('#tab_permissions').hide();
			$('#link_tab_permissions').removeClass('current');		
			
			<s:if test="eList.size > 0">
				$('#tab_subscriptions').hide();
				$('#link_tab_subscriptions').removeClass('current');
			</s:if>
			<s:if test="switchTos.size > 0">
				$('#tab_switch').hide();
				$('#link_tab_switch').removeClass('current');
			</s:if>
			
			$('#'+tabName).show();
			$('#link_'+tabName).addClass('current');
		}
		
		$(function () {
			$('#navlist').delegate('#link_tab_profile', 'click', function(e) {
				e.preventDefault();
				showTab('tab_profile');
			}).delegate('#link_tab_switch', 'click', function(e) {
				e.preventDefault();
				showTab('tab_switch');
			}).delegate('#link_tab_subscriptions', 'click', function(e) {
				e.preventDefault();
				showTab('tab_subscriptions');								
			}).delegate('#link_tab_permissions', 'click', function(e) {
				e.preventDefault();
				showTab('tab_permissions');
			});
			
			$('#departmentSuggest').autocomplete('UserProfileAjax!department.action?user=<s:property value="user.id"/>').result(function(event, data){
				$('#departmentRole').val(data[3])
			});
		});
	</script>
</head>
<body>
	<h1>
		<s:text name="ProfileEdit.title" />
		:
		<s:property value="u.username" />
	</h1>

	<div id="internalnavcontainer">
		<ul id="navlist">
			<li>
				<a id="link_tab_profile" href="#" class="current">
					<s:text name="button.Edit" />
				</a>
			</li>
			<li>
				<a href="ChangePassword.action?source=profile&user=<s:property value="u.id"/>" id="profile_edit_changePassword"> 
					<s:text	name="button.password" />
				</a>
			</li>
			<s:if test="switchTos.size > 0">
				<li>
					<a id="link_tab_switch" href="#">
						<s:text name="ProfileEdit.label.SwitchAccounts" />
					</a>
				</li>
			</s:if>

			<s:if test="eList.size > 0">
				<li>
					<a id="link_tab_subscriptions" href="#">
						<s:text name="ProfileEdit.label.EmailSubscriptions" />
					</a>
				</li>
			</s:if>

			<s:if test="!permissions.contractor">
				<li><a id="link_tab_permissions" href="#">
						<s:text name="ProfileEdit.label.Permissions" />
					</a>
				</li>
			</s:if>
		</ul>
	</div>

	<s:include value="../actionMessages.jsp"></s:include>

	<s:if test="u.forcePasswordReset">
		<div class="alert">
			<s:text name="ProfileEdit.alert.ChangePasswordRequired" />
		</div>
	</s:if>

	<div id="tab_profile">
		<table style="width: 100%">
			<tr>
				<td style="width: 50%;">
					<s:form id="saveProfileForm" cssClass="form">
						<s:hidden name="url" />
						<s:hidden name="u" value="%{u.id}" />

						<fieldset>
							<h2>
								<s:text name="ProfileEdit.Profile.heading" />
							</h2>

							<ol>
								<li>
									<label><s:text name="ProfileEdit.AssignedToAccount"></s:text>:</label>
									<s:property value="u.account.name" />
								</li>
								<li>
									<s:textfield name="u.name" theme="form" />
								</li>
								<li>
									<s:textfield id="departmentSuggest" name="u.department" size="15" theme="formhelp" />
								</li>
								<li>
									<s:textfield name="u.email" theme="form" />
								</li>
								<li>
									<s:textfield name="u.username" size="30" onchange="checkUsername(this.value);" theme="form" /> 
									<span id="username_status"></span>
								</li>
								<li>
									<s:textfield name="u.phone" theme="form" />
								</li>
								<li>
									<s:textfield name="u.fax" theme="form" />
								</li>

								<s:if test="u.account.demo || u.account.admin || i18nReady">
									<li><s:select name="u.locale"
											list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
											listValue="@org.apache.commons.lang3.StringUtils@capitalize(getDisplayName(language))"
											theme="form" /></li>
								</s:if>

								<li><s:select name="u.timezone" id="timezone"
										value="u.timezone.iD" theme="form"
										list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES" /></li>

								<li>
									<label><s:text name="global.CreationDate" />:</label>
									<s:date name="u.creationDate" />
								</li>
								<pics:toggle name="<%= FeatureToggle.TOGGLE_V7MENUS %>">
									<li>
										<label> <s:text name="User.useDynamicReport" /></label>
										<s:checkbox id="usingDynamicReports" name="usingDynamicReports" value="u.usingDynamicReports" />
									</li>
								</pics:toggle>
							</ol>
						</fieldset>

						<fieldset class="form submit">
							<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="save" />						
								
							<a class="change-password btn" href="ChangePassword.action?source=profile&user=<s:property value="u.id"/>" id="profile_edit_changePassword2">
								<s:text name="button.password" />
							</a>
						</fieldset>
						
					</s:form>
					<!-- See if this user has the RestApi permission, which means it is a special (non-human) API user. If so, allow it to (re)generate the API key. -->				
					<s:if test="permissions.hasPermission(@com.picsauditing.access.OpPerms@RestApi)">
						<s:include value="user_api_key.jsp" />
					</s:if>
				</td>
				<td style="width: 20px;">&nbsp;</td>
				<td style="vertical-align: top;">
					<h3>
						<s:text name="ProfileEdit.RecentLogins" />
					</h3>

					<table class="report" style="position: static;">
						<thead>
							<tr>
								<th><s:text name="Login.LoginDate" /></th>
								<th><s:text name="Login.IPAddress" /></th>
								<s:if test="permissions.isDeveloperEnvironment()">
									<th><s:text name="Login.Server" /></th>
								</s:if>
								<th><s:text name="global.Notes" /></th>
							</tr>
						</thead>
						<tbody>

							<s:iterator value="recentLogins">
								<tr>
									<td><s:date name="loginDate" /></td>
									<td><s:property value="remoteAddress" /></td>
									<s:if test="permissions.isDeveloperEnvironment()">
										<td>
											<s:property value="serverAddress" />
										</td>
									</s:if>
									<td><s:if test="admin.id > 0">
											<s:text name="Login.LoginBy">
												<s:param value="admin.name" />
												<s:param value="admin.account.name" />
											</s:text>
										</s:if> <s:if test="successful == 'N'">
											<s:text name="ProfileEdit.message.IncorrectPasswordAttempt" />
										</s:if></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>
	
	<s:if test="!permissions.contractor">
		<div id="tab_switch" style="display: none;">
			<s:include value="user_switch_accts.jsp" />
		</div>
	</s:if>

	<s:if test="!permissions.contractor">
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
		</div>
	</s:if>

	<s:if test="goEmailSub">
		<script type="text/javascript">
			$(function() {
				showTab('tab_subscriptions');
			 });
			</script>
	</s:if>

	<div id="tab_subscriptions" style="display: none;">
		<s:iterator value="eList" status="stat">
			<div id="td<s:property value="subscription"/>"
				<s:if test="#stat.even">class="shaded"</s:if>>
				<s:include value="../mail/user_email_subscription.jsp" />
			</div>
		</s:iterator>
	</div>
</body>