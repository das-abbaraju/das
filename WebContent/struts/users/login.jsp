<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
	<head>
		<title><s:text name="global.Login" /></title>
		<meta name="help" content="Logging_In">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/login.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/login.js"></script>
	</head>
	<body>
	
		<s:if test="configEnvironment"><!-- We can remove this once we've release i18n -->
			<ul class="locales">
				<li>
					<a href="?request_locale=en">English</a>
				</li>
				<li>
					<a href="?request_locale=fr">Français</a>
				</li>
				<li>
					<a href="?request_locale=es">Español</a>
				</li>
			</ul> 
		</s:if>
		
		<s:include value="login_form.jsp"/>
	
	</body>
</html>