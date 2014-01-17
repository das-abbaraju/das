<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>PICS - <decorator:title default="PICS" /></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="/bootstrap3/css/style.css?v=${version}">

    <%--Make ie8 corrections asap--%>
    <!--[if lt IE 9]><script src="/bootstrap3/js/vendor/respond.js?v=${version}"></script><![endif]-->

    <!--[if lt IE 8]><link rel="stylesheet" href="/bootstrap3/css/vendor/font-awesome-ie7.css?v=${version}"><![endif]-->
    <script src="/bootstrap3/js/vendor/modernizr-2.6.1.min.js?v=${version}"></script>
</head>
<%--<body id="${actionName}_${methodName}_page" class="${actionName}-page page">--%>
<body id="${uniquePageId}_page" class="${pageId}-page page" data-is-bootstrap="true" data-spy="scroll" data-offset-top="200" data-target="#side-navigation">

<header>
    <s:action name="Menu!menu" executeResult="true" />
</header>

<div id="main" role="main" class="container">
    <decorator:body />
</div>

<footer>
    <s:include value="/struts/layout/_environment.jsp" />
</footer>

<%-- include javascript translations --%>
<s:action name="TranslateJS2" executeResult="true" />

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script>google.load('visualization', '1.0', {'packages':['corechart']});</script>
<script src="/bootstrap3/js/script.js?v=${version}"></script>
<!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
<script>
    var _gaq=[['_setAccount','UA-2785572-4'],['_trackPageview']];
    (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
        g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
        s.parentNode.insertBefore(g,s)}(document,'script'));
</script>
</body>
</html>