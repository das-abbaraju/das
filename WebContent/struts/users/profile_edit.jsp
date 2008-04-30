<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Edit Profile</title>
</head>
<body>
<h1>Edit Profile</h1>
<s:form>
<s:hidden name="u.id" />
Assigned to account: <s:property value="u.account.name" /><br />
Display name: <s:textfield name="u.name" /><br />
Email address: <s:textfield name="u.email" size="30" /><br />
Username: <s:textfield name="u.username" /><br />
Password: <s:password name="password1" value="" /><br />
Confirm Password: <s:password name="password2" value="" /><br />
Created: <s:date name="u.dateCreated" /><br />
Last login: <s:date name="u.lastLogin" /><br />
<s:submit value="Save Profile" />
</s:form>
</body>
</html>
