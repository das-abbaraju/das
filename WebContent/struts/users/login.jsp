<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title><s:text name="global.Login" /></title>
	<meta name="help" content="Logging_In">
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/login.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp"/>
	
</head>
<body>
	<s:include value="_supportedLocales.jsp" />
	<s:include value="login_form.jsp"/>

    <script type="text/javascript" src="js/login.js?v=${version}"></script>
</body>