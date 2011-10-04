<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!doctype html>
	<head>
		<meta charset="utf-8">
		
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta name="description" content="">
		<meta name="author" content="">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/markdown.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/layout.css?v=${version}" />
		<!--[if !IE 6]><!--><link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=${version}" /><!--<![endif]-->
		<link rel="stylesheet" type="text/css" media="screen" href="css/form.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/registration.css?v=${version}" />
		
		<!--CSS FIXES FOR INTERNET EXPLORER -->
		<!--[if IE]><link rel="stylesheet" href="css/ie.css" type="text/css" /><![endif]-->
		<!--[if lt IE 7]><link rel="stylesheet" href="css/ie6.css" type="text/css" /><![endif]-->
	</head>
	<body id="${actionName}-page">
		<div id="container">
			
			<s:include value="/struts/layout/header.jsp" />
			
			<div id="main" role="main">
				<decorator:body />
			</div>
			
			<s:include value="/struts/layout/footer.jsp" />
		</div>

		<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if necessary -->
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
		<script>window.jQuery || document.write('<script src="js/libs/jquery-1.6.4.min.js">\x3C/script>')</script>
		
		<s:include value="/struts/layout/include_javascript.jsp" />
	</body>
</html>