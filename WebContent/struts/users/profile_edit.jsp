<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript">
	function checkUsername(username) {
		$('username_status').innerHTML = '<img src="images/ajax_process.gif" width="16" height="16" /> checking availability of username...';
		pars = 'userID=<s:property value="u.id"/>&username=' + username;
		var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {
			method :'get',
			parameters :pars
		});
	}

	function save(subscription, id, timeperiod) {
	    var pars = "eu.id="+id+"&eu.subscription="+subscription;
	    if($('add'+subscription).checked)
		    pars += "&addsubscription=true";
	    else
		    pars += "&addsubscription=false";

		if(timeperiod != null) {
			pars += '&sPeriod='+$(timeperiod).value;	
		}	
	    var divName = 'td'+subscription;
		startThinking({'div':divName});

		var myAjax = new Ajax.Updater(divName, 'UserEmailSubscriptionSaveAjax.action', {
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
			new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}

	function showTab(tabName) {
		$('tab_profile').hide();
		$('link_tab_profile').removeClassName('current');
		$('tab_permissions').hide();
		$('link_tab_permissions').removeClassName('current');
		<s:if test="eList.size > 0">
		$('tab_subscriptions').hide();
		$('link_tab_subscriptions').removeClassName('current');
		</s:if>
		<s:if test="switchTos.size > 0">
		$('tab_switch').hide();
		$('link_tab_switch').removeClassName('current');
		</s:if>
		
		$(tabName).show();
		$('link_'+tabName).addClassName('current');
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

<div id="tab_profile">
<table style="width: 100%">
	<tr>
		<td><s:include value="profile_edit_save.jsp" /></td>
		<td style="width: 20px;">&nbsp;</td>
		<td style="vertical-align:top;">
		
		<h3>Recent Logins</h3>
		<table class="report">
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
	<s:iterator value="eList">
		<table>
			<tr>
				<td>
					<input id="add<s:property value="subscription"/>" type="checkbox" onclick="save('<s:property value="subscription"/>', <s:property value="id"/>, null)" <s:if test="timePeriod.toString() != 'None'">checked</s:if>/>
				</td>
				<td>
					<b><u><s:property value="subscription.description"/></u></b>
					<br/><s:property value="subscription.longDescription"/>
					<br/>
					<span id="td<s:property value="subscription"/>">
						<s:include value="../mail/user_email_subscription.jsp"></s:include>
					</span>
				</td>
			</tr>
		</table>
		<br/>
	</s:iterator>
</div>

</body>
</html>
