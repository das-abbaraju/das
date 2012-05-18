<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!doctype html>
	<head>
		<meta charset="utf-8">
		
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta name="description" content="">
		<meta name="author" content="">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/markdown.css?v=${version}" />
		<!--[if !IE 6]><!--><link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=${version}" /><!--<![endif]-->
		
        
		<link rel="stylesheet" type="text/css" href="css/layout_print.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="css/form.css?v=${version}" />
		<link rel="stylesheet" type="text/css" href="css/contractor_agreement_print.css?v=${version}" />
		<link rel="stylesheet" type="text/css" href="css/contractor_flag_matrix_print.css?v=${version}" />
		
		<!-- CSS FIXES FOR INTERNET EXPLORER -->
		<!--[if IE]><link rel="stylesheet" href="css/ie.css?v=${version}" type="text/css" /><![endif]-->
		<!--[if lt IE 7]><link rel="stylesheet" href="css/ie6.css?v=${version}" type="text/css" /><![endif]-->
		
		<!-- JS FIXES FOR INTERNET EXPLORER -->
		<!--[if lt IE 9]>
		<script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>
	<body id="${actionName}-page" class="print-page">
		<header>
			<a href="javascript:;" class="btn primary print" onclick="window.print()">Print</a>
		</header>
		<div id="container">
			<div id="main" role="main">
				<div>
					<img src="images/logo_sm.png" alt="Home" class="logo"/>
				</div>
				
				<decorator:body />
			</div>
		</div>
	</body>
</html>