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
</script>

</head>
<body>
<h1>Edit Profile</h1>
<s:include value="../actionMessages.jsp" />

<table style="width: 100%">
	<tr>
		<td><s:form cssStyle="width: 500px">
			<s:hidden name="u.id" />
			<fieldset class="form"><legend><span>Profile</span></legend>
			<ol>
				<li><label>Assigned to account:</label> <s:property value="u.account.name" /></li>
				<li><label for="u.name">Display name:</label> <s:textfield name="u.name" /></li>
				<li><label for="u.email">Email address:</label> <s:textfield name="u.email" size="30" /></li>
				<li><label for="u.phone">Phone:</label> <s:textfield name="u.phone" size="20" /></li>
				<li><label for="u.fax">Fax:</label> <s:textfield name="u.fax" size="20" /></li>
				<li><label>Created:</label> <s:date name="u.creationDate" /></li>
				<li><label>Last login:</label> <s:date name="u.lastLogin" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Username &amp; Password</span></legend>
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
			<div class="buttons">
			<button id="saveButton" class="positive" value="Save Profile" name="button" type="submit">Save Profile</button>
			</div>
			</fieldset>
		</s:form></td>
		<td style="width: 20px;">&nbsp;</td>
		<td style="vertical-align:top;">
		<s:if test="switchTos.size > 0">
			<h3>Switch Accounts</h3>
			<div>
			<img src="images/beta.jpg" width="98" height="100" style="float: right;"
				title="This is a new feature. Please send us your feedback or suggestions." />
			<ol>
				<li><a href="Login.action?button=switch&switchToUser=<s:property value="u.id" />"><s:property
					value="u.account.name" /> - Primary</a></li>
				<s:iterator value="switchTos">
					<li><a href="Login.action?button=switch&switchToUser=<s:property value="switchTo.id" />"><s:property
						value="switchTo.account.name" /> - <s:property value="switchTo.name" /></a></li>
				</s:iterator>
			</ol>
			</div>
		</s:if>
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
		
		<h3>Permissions</h3>
		<table class="report" style="margin-top: 20px">
			<s:iterator value="permissions.permissions">
				<tr>
					<td title="<s:property value="opPerm.helpText" />"><s:property value="opPerm.description" /></td>
					<td><s:if test="viewFlag">View</s:if></td>
					<td><s:if test="editFlag">Edit</s:if></td>
					<td><s:if test="deleteFlag">Delete</s:if></td>
				</tr>
			</s:iterator>
		</table>
		<s:if test="permissions.operatorCorporate">
			<h3>Visible Audit/Policy Types</h3>
			<div>
			<ul>
				<s:iterator value="viewableAuditsList">
					<li><s:property value="auditName" /></li>
				</s:iterator>
			</ul>
			</div>
		</s:if></td>
	</tr>
</table>

<div class="clear"></div>
</body>
</html>
