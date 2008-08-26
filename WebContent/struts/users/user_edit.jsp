<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Edit User</title>
</head>
<body>
<h1>Edit User</h1>
<s:include value="../actionMessages.jsp" />

<p style="font-style: italic">Note: Users must relogin for changes to take effect</p>
<div id="ajaxstatus" style="height: 20px;"></div>
<s:form>
	<s:hidden name="user.id" />
	<s:hidden name="user.accountID" />
	<s:hidden name="user.isGroup" />
<table class="form">
<pics:permission perm="EditUsers" type="Edit">
	<tr>
	  <td>&nbsp;</td>
	  <td><input name="button" type="button" class="forms" value="Save" onclick="saveUser();"
	  	style="font-size: 14px; font-weight: bold;"></td>
	</tr>
</pics:permission>

</s:form>

</body>
</html>
