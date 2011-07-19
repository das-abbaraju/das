<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="global.Login" /></title>
<meta name="help" content="Logging_In">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('#username').focus();
});
</script>
<style type="text/css">
#locales {
	position: relative;
	top: -30px
}
#locales a, #locales a:visited, #locales a:hover, #locales a:active {
	margin: 15px;
	padding: 4px;
	text-decoration: none;
	font-weight: bold;
	border: 1px solid white;
	color: gray;
}
#locales a:hover, #locales a:active {
	border: 1px solid gray;
	background-color: #F0F0F0;
}
</style>
</head>
<body>

<s:include value="login_form.jsp"/>

</body>
</html>