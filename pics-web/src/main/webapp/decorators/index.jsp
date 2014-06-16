<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>PICS - <decorator:title default="PICS" /></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="/angular/build/style.css"/>

    <script src="//maps.google.com/maps/api/js?libraries=places&sensor=false"></script>
    <script src="/angular/build/script.js"></script>
</head>
<body>
<div id="main">
    <header>
        <s:action name="Menu!bootstrap3Menu" executeResult="true" />
        <s:action name="Menu!mobileMenu" executeResult="true" />
    </header>

    <div ng-app="PICSApp">
        <div ng-view></div>
    </div>

    <footer>
        <s:include value="/struts/layout/_environment.jsp" />
    </footer>
</div>
</body>
</html>