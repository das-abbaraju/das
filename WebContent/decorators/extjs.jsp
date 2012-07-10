<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!doctype html>
	<head>
		<meta charset="utf-8">
		
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta name="description" content="">
		<meta name="author" content="">
		
		<link rel="stylesheet" type="text/css" href="js/pics/resources/css/ext-all.css">
		
		<script type="text/javascript" src="js/pics/extjs/bootstrap.js"></script>
		
		<decorator:head />
	</head>
	<body id="${actionName}-page">
		<decorator:body />
	</body>
</html>