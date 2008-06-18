<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Edit Profile</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css"/>
</head>
<body>
<h1>Edit Profile</h1>
<div id="info" <s:if test="message == null">style="display: none"</s:if>><s:property value="message"/></div>
<br clear="all" >
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
			<td><s:textfield name="u.username" /></td>
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
				<td class="center"><s:submit value="Save Profile" name="save" onclick="$('info').hide(); return true;"/></td>
			</tr>
		</tfoot>
	</table>
</s:form>
</body>
</html>
