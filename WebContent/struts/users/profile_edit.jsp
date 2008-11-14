<%@ taglib prefix="s" uri="/struts-tags"%>
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
	<fieldset>
	<legend><span>Profile</span></legend>
	<ol>
		<li><label>Assigned to account:</label>
			<s:property value="u.account.name" /></li>
		<li><label for="u.name">Display name:</label>
			<s:textfield name="u.name" /></li>
		<li><label for="u.email">Email address:</label>
			<s:textfield name="u.email" size="30"/></li>
		<li><label>Created:</label>
			<s:date name="u.dateCreated" /></li>
		<li><label>Last login:</label>
			<s:date name="u.lastLogin" /></li>
	</ol>
	</fieldset>
	<fieldset>
	<legend><span>Username &amp; Password</span></legend>
	<ol>
		<li><label for="u.username">Username:</label>
			<s:textfield name="u.username" onblur="checkUsername(this.value);"/>
			<div id="username_status">&nbsp;</div></li>
		<li><label for="password1">Password:</label>
			<s:password name="password1" value="" /></li>
		<li><label for="password2">Confirm Password:</label>
			<s:password name="password2" value="" /></li>
	</ol>
	</fieldset>
	<fieldset class="submit">
		<div class="buttons">
			<button id="saveButton" class="positive" value="Save Profile" name="button" type="submit">Save Profile</button>
		</div>
	</fieldset>
</s:form>

</body>
</html>
