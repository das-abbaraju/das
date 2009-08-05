<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>

<s:if test="permissions.accountId == u.account.id">
	<p><label>Account:</label> <s:property value="u.account.name" /></p>
</s:if>
<s:else>
	<p><label>Current Account:</label> <s:property
		value="permissions.accountName" /></p>
	<p><label>Primary Account:</label> <s:property
		value="u.account.name" /></p>
</s:else>

<p><label>Display name:</label> <s:property value="u.name" /></p>

<p><label>Email address:</label> <s:property value="u.email" /></p>

<p><label>Username:</label> <s:property value="u.username" /></p>

<a href="ProfileEdit.action">Edit Profile</a>
</body>
</html>
