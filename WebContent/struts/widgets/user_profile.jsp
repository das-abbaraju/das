<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<body>
<s:if test="permissions.accountId == u.account.id">
	<p><label><s:text name="global.Account" />:</label> <s:property value="u.account.name" /></p>
</s:if>
<s:else>
	<p><label><s:text name="global.AccountCurrent" />:</label> <s:property
		value="permissions.accountName" /></p>
	<p><label><s:text name="global.AccountPrimary" />:</label> <s:property
		value="u.account.name" /></p>
</s:else>

<p><label><s:text name="ProfileEdit.u.name" />:</label> <s:property value="u.name" /></p>

<p><label><s:text name="ProfileEdit.u.email" />:</label> <s:property value="u.email" /></p>

<p><label><s:text name="global.Username" />:</label> <s:property value="u.username" /></p>
<label><s:text name="ProfileEdit.u.phone" />:</label> <s:property value="u.phone" />
<s:if test="u.fax != null">
	&nbsp;&nbsp;&nbsp;&nbsp;
	<label><s:text name="ProfileEdit.u.fax" />:</label> <s:property value="u.fax" />
</s:if>
<p><label><s:text name="ProfileEdit.u.timezone" />:</label> <s:property value="u.timezone.iD" /></p>
<a href="ProfileEdit.action" class="edit"><s:text name="ProfileEdit.Profile.heading" /></a>

<s:if test="eList.size > 0">
	<br/><a href="ProfileEdit.action?goEmailSub=true" class="edit">My Email Subscriptions</a>
</s:if>
</body>
</html>
