<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css"/>

<script type="text/javascript">
function checkUsername(username) {
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID=<s:property value="u.id"/>&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}
</script>

</head>
<body>
<h1>Edit Profile</h1>
<s:include value="../actionMessages.jsp" />

<s:form>
	<s:hidden name="u.id" />
	<table class="forms">
		<tr>
			<th>Assigned to account:</th>
			<td><s:property value="u.account.name" /></td>
		</tr>
		<tr class="odd">
			<th>Display name:</th>
			<td><s:textfield name="u.name" /></td>
		</tr>
		<tr>
			<th>Email address:</th>
			<td><s:textfield name="u.email" size="30" /></td>
		</tr>
		<tr class="odd">
			<th>Username:</th>
			<td><s:textfield name="u.username" onblur="checkUsername(this.value);"/>
			<div id="username_status"></div></td>
		</tr>
		<tr>
			<th>Password:</th>
			<td><s:password name="password1" value="" /></td>
		</tr>
		<tr class="odd">
			<th>Confirm Password:</th>
			<td><s:password name="password2" value="" /></td>
		</tr>
		<tr>
			<th>Created:</th>
			<td><s:date name="u.dateCreated" /></td>
		</tr>
		<tr class="odd">
			<th>Last login:</th>
			<td><s:date name="u.lastLogin" /></td>
		</tr>
		<tfoot>
			<tr>
				<th></th>
				<td class="center">
				<div class="buttons">
					<s:submit id="saveButton" value="Save Profile" name="button" type="button" cssClass="positive" onclick="$('info').hide(); return true;"/>
				</div>
				</td>
			</tr>
		</tfoot>
	</table>
</s:form>


</body>
</html>
