<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:property value="report.summary"/></title>
        <!-- <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css"> -->
		
		<style>
		.x-body {
			background: #FFFFFF;
		}
		</style>
	</head>
	<body>
		<script type="text/javascript">
			var availableFields = <s:property value="availableFields" escape="false" />;
			var reportParameters = <s:property value="report.toJSON(true)" escape="false" />;
			var gridColumns = <s:property value="gridColumns" escape="false" />;
		</script>
		<script type="text/javascript" src="js/pics/app.js"></script>
	</body>
</html>