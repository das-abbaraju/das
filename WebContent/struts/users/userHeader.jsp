<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>


<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ProfileEdit.action"
		<s:if test="requestURI.contains('profile_edit')">class="current"</s:if>>Profile Edit</a></li>
	<li><a href="UserPermissions.action"
		<s:if test="requestURI.contains('user_permissions')">class="current"</s:if>>Permissions & Login</a></li>
</ul>
</div>

<s:include value="../actionMessages.jsp"></s:include>
