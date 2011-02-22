<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="EditProfile.title"></s:text></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
	function checkUsername(username) {
		startThinking({div: 'username_status', message: 'checking availability of username...'});
		var data = {userID: <s:property value="u.id"/>, username: username};
		$('#username_status').load('user_ajax.jsp', data);
	}

	<s:if test="!permissions.contractor">
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
	</s:if>
</script>

</head>
<body>
<h1><s:text name="EditProfile.title" /></h1>
<s:if test="!permissions.contractor">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a id="link_tab_profile" href="#" class="current" onclick="showTab('tab_profile'); return false;">Edit</a></li>
	<s:if test="switchTos.size > 0">
	<li><a id="link_tab_switch" href="#" onclick="showTab('tab_switch'); return false;">Switch Accounts</a></li>
	</s:if>
	<s:if test="eList.size > 0">
		<li><a id="link_tab_subscriptions" href="#" onclick="showTab('tab_subscriptions'); return false;">Email Subscriptions</a></li>
	</s:if>
	<li><a id="link_tab_permissions" href="#" onclick="showTab('tab_permissions'); return false;">Permissions</a></li>
</ul>
</div>
</s:if>
<s:include value="../actionMessages.jsp"></s:include>

<s:if test="u.forcePasswordReset">
<div class="alert">
	You are required to change your password.
</div>
</s:if>

<div id="tab_profile">
<table style="width: 100%">
	<tr>
		<td>
		
<s:form id="saveProfileForm">
	<s:hidden name="url"/>
	<s:hidden name="u.id" />
	<fieldset class="form">
	<h2 class="formLegend">Profile</h2>
	<ol>
		<li><label>Assigned to account:</label> <s:property value="u.account.name" /></li>
		<li><label for="u.name">Display name:</label> <s:textfield name="u.name" /></li>
		<li><label for="u.email">Email address:</label> <s:textfield name="u.email" size="30" /></li>
		<li><label for="u.phone">Phone:</label> <s:textfield name="u.phone" size="20" /></li>
		<li><label for="u.fax">Fax:</label> <s:textfield name="u.fax" size="20" /></li>
		<li><label for="u.locale">Language:</label> <s:select
			list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
			name="u.locale" listValue="displayName"></s:select>
		</li>
		<li><label for="u.timezone">Timezone:</label> <s:select name="u.timezone"
			list="@com.picsauditing.util.TimeZoneUtil@getTimeZoneSelector()" listKey="key" listValue="value"></s:select>
		</li>
		<li><label>Profile Created:</label> <s:property value="formatDate(u.creationDate)" /></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Username &amp; Password</h2>
	<ol>
		<li><label for="u.username">Username:</label> <s:textfield name="u.username"
			onchange="checkUsername(this.value);" />
		<div id="username_status">&nbsp;</div>
		</li>
		<li><label for="password1">Password:</label> <s:password name="password1" value="" /></li>
		<li><label for="password2">Confirm Password:</label> <s:password name="password2" value="" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div><input type="submit" class="picsbutton positive" name="button" value="Save Profile" /></div>
	</fieldset>
</s:form>
		
		</td>
		<td style="width: 20px;">&nbsp;</td>
		<td style="vertical-align:top;">
		
		<h3>Recent Logins</h3>
		<table class="report" style="position: static;">
		<thead>
			<tr>
				<th>Login Date/Time</th>
				<th>IP Address</th>
				<th>Notes</th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="recentLogins">
			<tr>
				<td><s:date name="loginDate"/></td>
				<td><s:property value="remoteAddress"/></td>
				<td>
					<s:if test="admin.id > 0">Login by <s:property value="admin.name"/> from <s:property value="admin.account.name"/></s:if>
					<s:if test="successful == 'N'">Incorrect password attempt</s:if>
				</td>
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
	<h3>Switch Accounts</h3>
	<table class="report">
		<thead>
			<tr>
				<td>Account</td>
				<td>User/Role</td>
			</tr>
		</thead>
		<tbody>
			<tr><td>
				<a href="Login.action?button=switch&switchToUser=<s:property value="u.id" />"><s:property
				value="u.account.name" /></a>
			</td>
			<td>Primary</td>
			</tr>
			<s:iterator value="switchTos">
			<tr>
				<td><a href="Login.action?button=switch&switchToUser=<s:property value="switchTo.id" />"><s:property
				value="switchTo.account.name" /></a></td>
				<td><s:property value="switchTo.name" /></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</div>
</s:if>

<s:if test="!permissions.contractor">
<div id="tab_permissions" style="display: none;">
<table style="width: 100%">
	<tr>
		<td>
		<h3>Permissions</h3>
		<table class="report">
			<thead>
				<tr>
					<th>Permission Name</th>
					<th>View</th>
					<th>Edit</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
			<s:iterator value="permissions.permissions">
				<tr>
					<td title="<s:property value="opPerm.helpText" />"><s:property value="opPerm.description" /></td>
					<td><s:if test="viewFlag">View</s:if></td>
					<td><s:if test="editFlag">Edit</s:if></td>
					<td><s:if test="deleteFlag">Delete</s:if></td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
		</td>
		<td>
			<h3>Visible Audit &amp; Policy Types</h3>
			<div>
			<s:if test="permissions.operatorCorporate">
			<ul>
				<s:iterator value="viewableAuditsList">
					<li><s:property value="auditName" /></li>
				</s:iterator>
			</ul>
			</s:if>
			<s:else>
				You are a PICS Employee and have access to all Audit Types.
			</s:else>
			</div>
			<s:if test="permissions.admin && permissions.shadowedUserID != permissions.userId">
				<h3>Shadowing</h3>
				<div>You are currently shadowing <s:property value="permissions.shadowedUserName" />.</div>
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
<s:if test="!permissions.contractor">
<div id="tab_subscriptions" style="display: none;">
	<s:iterator value="eList" status="stat">
	<div id="td<s:property value="subscription"/>" <s:if test="#stat.even">class="shaded"</s:if>>
		<s:include value="../mail/user_email_subscription.jsp"/>
	</div>
	</s:iterator>
</div>
</s:if>
</body>
</html>
