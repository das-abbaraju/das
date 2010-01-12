<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Edit Profile</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=20091231" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
	function checkUsername(username) {
		startThinking({div: 'username_status', message: 'checking availability of username...'});
		var data = {userID: <s:property value="u.id"/>, username: username};
		$('#username_status').load('user_ajax.jsp', data);
	}

	function save(subscription, id, timeperiod) {
	    var data = {
    	    'eu.id': id, 
    	    'eu.subscription': subscription,
    	    'addSubscription': $('#add'+subscription).is(':checked')
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
</script>

</head>
<body>
<h1>Edit Profile</h1>

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

<s:include value="../actionMessages.jsp"></s:include>

<s:if test="u.forcePasswordReset">
<div class="alert">
	You are required to change your password.
</div>
</s:if>

<div id="tab_profile">
<table style="width: 100%">
	<tr>
		<td><s:include value="profile_edit_save.jsp" /></td>
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
		</td>
	</tr>
</table>
</div>

<div id="tab_subscriptions" style="display: none;">
	<s:iterator value="eList" status="stat">
	<div id="td<s:property value="subscription"/>" <s:if test="#stat.even">class="shaded"</s:if>>
		<s:include value="../mail/user_email_subscription.jsp"/>
	</div>
	</s:iterator>
</div>

</body>
</html>
