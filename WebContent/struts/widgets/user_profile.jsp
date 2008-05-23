<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>

<p><label>Account:</label>
<s:property value="u.account.name" /></p>

<p><label>Display name:</label>
<s:property value="u.name" /></p>

<p><label>Email address:</label>
<s:property value="u.email" /></p>

<p><label>Username:</label>
<s:property value="u.username" /></p>

<a href="ProfileEdit.action">Edit Profile</a>
</body>
</html>
