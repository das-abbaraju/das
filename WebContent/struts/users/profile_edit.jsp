<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css"/>

<script type="text/javascript">
function checkUsername(username) {
	$('ProfileEdit_save').writeAttribute('disabled','true');
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID=<s:property value="u.id"/>&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars,
				onComplete: function(transport) {
					if($('username_status').innerHTML.indexOf('is NOT available. Please choose a different username.') == -1)
					{
						$('ProfileEdit_save').writeAttribute('disabled', null);						
					}
				}
			});
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
		<tr>
			<th>Display name:</th>
			<td><s:textfield name="u.name" /></td>
		</tr>
		<tr>
			<th>Email address:</th>
			<td><s:textfield name="u.email" size="30" /></td>
		</tr>
		<tr>
			<th>Username:</th>
			<td><s:textfield name="u.username" onblur="checkUsername(this.value);"/>
			<div id="username_status"></div></td>
		</tr>
		<tr>
			<th>Password:</th>
			<td><s:password name="password1" value="" /></td>
		</tr>
		<tr>
			<th>Confirm Password:</th>
			<td><s:password name="password2" value="" /></td>
		</tr>
		<tr>
			<th>Created:</th>
			<td><s:date name="u.dateCreated" /></td>
		</tr>
		<tr>
			<th>Last login:</th>
			<td><s:date name="u.lastLogin" /></td>
		</tr>
		<tfoot>
			<tr>
				<th></th>
				<td class="center"><s:submit value="Save Profile" name="button" onclick="$('info').hide(); return true;"/></td>
			</tr>
		</tfoot>
	</table>
</s:form>


</body>
</html>
