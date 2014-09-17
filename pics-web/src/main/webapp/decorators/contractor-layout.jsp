<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="has_contractor_submenu" value="%{permissions.operator || permissions.picsEmployee ? true : false}" />
<s:set var="contractor_submenu_flag" value="%{#has_contractor_submenu ? 'data-view=\"contractor\"' : ''}" />

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

        <link rel="stylesheet" href="v7/css/style.css?v=${version}">
        <!--[if lt IE 8]><link rel="stylesheet" href="v7/css/vendor/font-awesome-ie7.css?v=${version}"><![endif]-->
        
        <script src="v7/js/vendor/modernizr-2.6.1.min.js?v=${version}"></script>
    </head>
    <body id="${actionName}_${methodName}_page" class="${actionName}-page page" ${contractor_submenu_flag}>
    
        <header>
            <s:action name="Menu!menu" executeResult="true" />
            
            <s:if test="#has_contractor_submenu">
                <s:action name="Menu!contractorSubmenu" executeResult="true" />
            </s:if>
        </header>
        
        <div id="main" role="main" class="container">
            <decorator:body />
        </div>
        
        <footer>
            <s:include value="/struts/layout/_environment.jsp" />
        </footer>
        
        <%-- include javascript translations --%>
        <s:action name="TranslateJS2" executeResult="true" />

        <script src="v7/js/script.js?v=${version}"></script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <s:if test="isBetaEnvironment() || isStableEnvironment()">
            <script>
                var _gaq=[['_setAccount','UA-2785572-4'],['_trackPageview']];
                (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
                    g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
                    s.parentNode.insertBefore(g,s)}(document,'script'));
            </script>
        </s:if>
    </body>
</html>