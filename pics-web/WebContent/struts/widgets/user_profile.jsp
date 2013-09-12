<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="permissions.accountId == u.account.id">
	<p>
		<label><s:text name="global.Account" />:</label>
		<s:property value="u.account.name" />
	</p>
</s:if>
<s:else>
	<p>
		<label><s:text name="global.AccountCurrent" />:</label> 
		<s:property value="permissions.accountName" />
	</p>

	<p>
		<label><s:text name="global.AccountPrimary" />:</label> 
		<s:property	value="u.account.name" />
	</p>
</s:else>

<p>
	<label><s:text name="ProfileEdit.u.name" />:</label>
	<s:property value="u.name" />
</p>

<p>
	<label><s:text name="ProfileEdit.u.email" />:</label>
	<s:property value="u.email" />
</p>

<p>
	<label><s:text name="global.Username" />:</label>
	<s:property value="u.username" />
</p>

<p>
<label><s:text name="ProfileEdit.u.phone" />:</label>
	<s:property value="u.phone" />
	<s:if test="!isStringEmpty(u.fax)">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label><s:text name="ProfileEdit.u.fax" />:</label>
		<s:property value="u.fax" />
	</s:if>
</p>

<p>
	<label><s:text name="ProfileEdit.u.timezone" />:</label>
	<s:if test="u.timezone != null">
		<s:text name="%{@com.picsauditing.util.TimeZoneUtil@timeZones().get(u.timezone.iD)}" />
	</s:if>
</p>

<p>
	<a href="ProfileEdit.action" class="edit"><s:text name="ProfileEdit.Profile.heading" /></a>
</p>

<s:if test="eList.size > 0">
	<p>
		<a href="ProfileEdit.action?goEmailSub=true" class="edit"><s:text name="ProfileEdit.EmailSubscriptions" /></a>
	</p>
</s:if>
