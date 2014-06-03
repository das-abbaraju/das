<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" xmlns:ng="http://angularjs.org"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>PICS - <decorator:title default="PICS" /></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="/employee-guard/build/style.css"/>

    <%--Make ie8 corrections asap--%>
    <!--[if lt IE 9]>
        <script src="/bootstrap3/js/vendor/respond.js?v=${version}"></script>
        <link rel="stylesheet" href="/bootstrap3/css/ie8.css?v=${version}">
    <![endif]-->

    <!--[if lt IE 8]><link rel="stylesheet" href="/bootstrap3/css/vendor/font-awesome/font-awesome-ie7.css?v=${version}"><![endif]-->



    <script src="/employee-guard/build/script.js"></script>

</head>
<body>
    <div id="main">
        <header>
            <s:action name="Menu!bootstrap3Menu" executeResult="true" />
            <s:action name="Menu!mobileMenu" executeResult="true" />

        </header>
        <div id="ng-app" ng-app="EmployeeGUARD">
            <div ng-view></div>
        </div>
    </div>
</body>
</html>