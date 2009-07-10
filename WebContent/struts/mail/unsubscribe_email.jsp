<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Unsubscribe Email</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
</head>
<body>
<s:if test="actionErrors.size > 0">
	<div id="error">
	<s:iterator value="actionErrors">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionErrors = null"/>
</s:if>
<s:else>
	<div id=info>
	You have successfully unsubscribed from this Email<br/><br/>
	<a href="index.jsp" class="redMain">PICS Website</a> |
	<a href="Login.action" class="redMain">Login</a>
	</div>
</s:else>	
</body>
</html>