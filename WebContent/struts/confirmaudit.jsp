<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<title>Confirm Audit</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<style type="text/css">
.audit_confirm{
	font-size: 20px;
	width: 400px;
	height: 100px; 
	border:3px solid #A84D10;
	background-color: #CCC;
	text-align: center;
	margin: 0 auto;
}
</style>
</head>
<body>
<s:if test="!hasActionErrors()">
	<div class="audit_confirm">
		Thank You for Confirming this Audit.<br/><br/>
	
		<a href="index.jsp" class="redMain">PICS Website</a> |
		<a href="Login.action" class="redMain">Login</a>	
	</div>
</s:if>
<s:include value="actionMessages.jsp" />
</body>
</html>