<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!doctype html>
	<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
	<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
	<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
	<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
	<!-- Consider adding a manifest.appcache: h5bp.com/d/Offline -->
	<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
	<head>
	    <meta charset="utf-8">
	
	    <!-- Use the .htaccess and remove these lines to avoid edge case issues.
	        More info: h5bp.com/i/378 -->
	    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta name="description" content="">
	
		<!-- Mobile viewport optimized: h5bp.com/viewport -->
		<meta name="viewport" content="width=device-width">

        <link rel="stylesheet" type="text/css" media="screen" href="v7/css/libs/bootstrap.css?v=${version}" />
        <link rel="stylesheet" type="text/css" media="screen" href="v7/css/bootstrap-custom.css?v=${version}" />
        <link rel="stylesheet" type="text/css" media="screen" href="v7/css/libs/font-awesome.css?v=${version}" />
        
        <%-- custom --%>
        <link rel="stylesheet" type="text/css" media="screen" href="css/environment.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="v7/js/extjs/pics/resources/css/my-ext-theme-menu.css?v=${version}" />
        
        <link rel="stylesheet" type="text/css" href="v7/css/pics.css?v=${version}" />
        
        <decorator:head />
        
		<!-- All JavaScript at the bottom, except this Modernizr build.
		Modernizr enables HTML5 elements & feature detects for optimal performance.
		Create your own custom Modernizr build: www.modernizr.com/download/ -->
		<script src="v7/js/libs/modernizr-2.5.3.min.js"></script>
    </head>
    <body id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <jsp:include page="/struts/layout/header.jsp" />
            
        <div id="main" role="main" class="container-fluid">
            <decorator:body />
        </div>
        
		<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<script>window.jQuery || document.write('<script src="v7/js/libs/jquery-1.7.1.min.js"><\/script>')</script>
        
        <%-- libs --%>
        <script type="text/javascript" src="v7/js/libs/bootstrap.js"></script>
        <script type="text/javascript" src="v7/js/extjs/pics/extjs/ext-all.js"></script>
        
        <%-- custom --%>
        <script type="text/javascript" src="v7/js/pics/core/core.js"></script>
        <script type="text/javascript" src="v7/js/pics/layout/menu.js"></script>
        <script type="text/javascript" src="v7/js/pics/widgets/modal.js"></script>
        
        <script type="text/javascript" src="v7/js/pics/report/list-favorites-controller.js"></script>
        <script type="text/javascript" src="v7/js/pics/report/list-my-reports-controller.js"></script>
        <script type="text/javascript" src="v7/js/pics/report/search-controller.js"></script>
        
        <script type="text/javascript" src="v7/js/pics/report/report.js"></script>
    </body>
</html>