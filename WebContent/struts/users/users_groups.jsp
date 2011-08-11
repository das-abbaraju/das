<%@ taglib prefix="s" uri="/struts-tags"%>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/user_manage.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
var accountID = <s:property value="account.id" />;
var currentUserID = 0;
<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>
</script>
<s:if test="account.operatorCorporate">
	<a href="FacilitiesEdit.action?id=<s:property value="account.id"/>"><s:property value="account.name" /></a>
</s:if>
<s:if test="account.admin">PICS</s:if>
&gt; <a href="UsersManage.action?account=<s:property value="account.id"/>">Manage User Accounts</a>
<s:if test="user.id > 0">
&gt; <a href=""><s:property value="user.name" /></a>
</s:if>
<br/>
<s:if test="user.ownedPermissions.size() == 0 && user.groups.size() == 0">
	<div class="alert">Please add permission to this user</div>
</s:if>
<div id="permissionReport" style="width: 600px"><s:include value="user_save_permissions.jsp" /></div>
<div id="groupReport"><s:include value="user_save_groups.jsp" /></div>
