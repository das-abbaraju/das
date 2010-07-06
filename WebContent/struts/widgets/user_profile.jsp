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
<label>Phone:</label> <s:property value="u.phone" />
<s:if test="u.fax != null">
	&nbsp;&nbsp;&nbsp;&nbsp;
	<label>Fax:</label> <s:property value="u.fax" />
</s:if>
<p><label>TimeZone:</label> <s:property value="u.timezone" /></p>
<a href="ProfileEdit.action">Edit Profile</a>
<s:if test="eList.size > 0">
	<br/><a href="ProfileEdit.action?goEmailSub=true">Edit my Email Subscriptions</a>
</s:if>
</body>
</html>
