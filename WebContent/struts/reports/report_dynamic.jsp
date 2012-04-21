<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:property value="report.summary"/></title>
        <!-- <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css"> -->
        <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-custom.css?v=${version}">
	</head>
	<body>
		<script type="text/javascript">
			var availableFields = <s:property value="availableFields" escape="false" />;
			var reportParameters = <s:property value="report.toJSON(true)" escape="false" />;
		</script>
		<script type="text/javascript" src="js/pics/app.js?v=${version}"></script>
	</body>
</html>