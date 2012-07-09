<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!doctype html>
	<head>
		<meta charset="utf-8">
		
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta name="description" content="">
		<meta name="author" content="">
		
		<script type="text/javascript" src="js/pics/extjs/bootstrap.js"></script>
		<%-- <script type="text/javascript" src="js/pics/extjs/ext-all.js"></script> --%>
        
        <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css">
        <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme-menu.css">
        <link rel="stylesheet" type="text/css" href="v7/css/font-awesome.css">
        
        <%-- TODO: this should be refactored out of the custom css and done properly --%>
        <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-custom.css">
		
		<decorator:head />
	</head>
	<body id="${actionName}-page">
		<decorator:body />
	</body>
</html>