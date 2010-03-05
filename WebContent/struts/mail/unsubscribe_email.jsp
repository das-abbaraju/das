<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Unsubscribe Email</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
</head>
<body>
<s:if test="actionErrors.size > 0">
	<div class="error">
	<s:iterator value="actionErrors">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionErrors = null"/>
</s:if>
<s:else>
	<div id=info>
	You have successfully unsubscribed from this Email<br/>
	Click Here to <a href="ProfileEdit.action">Edit your Email Subscriptions</a> or here to login to <a href="Login.action" class="redMain">PICS Website</a>.<br/>  
	</div>
</s:else>	
</body>
</html>