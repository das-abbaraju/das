<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>Home</title>
        <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-custom.css?v=${version}">
	</head>
	<body>
		<script type="text/javascript">
			var dashboard = <s:property value="dashboard" escape="false" />;
		</script>
		<script type="text/javascript" src="js/pics/dashboard.js?v=${version}"></script>
	</body>
</html>