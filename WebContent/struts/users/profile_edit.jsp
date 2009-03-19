<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css"/>

<script type="text/javascript">
function checkUsername(username) {
	$('username_status').innerHTML = '<img src="images/ajax_process.gif" width="16" height="16" /> checking availability of username...';
	pars = 'userID=<s:property value="u.id"/>&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}
</script>

</head>
<body>
<h1>Edit Profile</h1>
<s:include value="../actionMessages.jsp" />

<s:form cssStyle="width: 500px">
	<s:hidden name="u.id" />
	<fieldset class="form">
	<legend><span>Profile</span></legend>
	<ol>
		<li><label>Assigned to account:</label>
			<s:property value="u.account.name" /></li>
		<li><label for="u.name">Display name:</label>
			<s:textfield name="u.name" /></li>
		<li><label for="u.email">Email address:</label>
			<s:textfield name="u.email" size="30"/></li>
		<li><label for="u.phone">Phone:</label>
			<s:textfield name="u.phone" size="20"/></li>
		<li><label for="u.fax">Fax:</label>
			<s:textfield name="u.fax" size="20"/></li>
		<li><label>Created:</label>
			<s:date name="u.creationDate" /></li>
		<li><label>Last login:</label>
			<s:date name="u.lastLogin" /></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<legend><span>Username &amp; Password</span></legend>
	<ol>
		<li><label for="u.username">Username:</label>
			<s:textfield name="u.username" onchange="checkUsername(this.value);"/>
			<div id="username_status">&nbsp;</div></li>
		<li><label for="password1">Password:</label>
			<s:password name="password1" value="" /></li>
		<li><label for="password2">Confirm Password:</label>
			<s:password name="password2" value="" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<div class="buttons">
			<button id="saveButton" class="positive" value="Save Profile" name="button" type="submit">Save Profile</button>
		</div>
	</fieldset>
	<pics:permission perm="DevelopmentEnvironment">
		<s:if test="u.switchTos.size > 0">
		<fieldset class="form">
		<legend><span>Switch Accounts</span></legend>
		<img src="images/beta.jpg" width="98" height="100" style="float: right;" title="This is a new feature. Please send us your feedback or suggestions." />
		<ol>
			<li><a href="Login.action?button=switch&switchToUser=<s:property value="u.id" />"><s:property value="u.account.name" /> - Primary</a></li>
			<s:iterator value="u.switchTos">
				<li><a href="Login.action?button=switch&switchToUser=<s:property value="switchTo.id" />"><s:property value="switchTo.account.name" /> - <s:property value="switchTo.name" /></a></li>
			</s:iterator>
		</ol>
		</fieldset>
		</s:if>
	</pics:permission>
</s:form>

</body>
</html>
