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

        <link rel="stylesheet" href="v7/css/style.css">
        
        <script src="v7/js/vendor/modernizr-2.6.1.min.js"></script>
    </head>
    <body id="${actionName}_${methodName}_page" class="${actionName}-page page">
    
        <header>
            <div class="navbar navbar-fixed-top">
                <div class="navbar-inner">
                    <nav class="container">
                        <a class="brand" href="/"><img src="/v7/img/logo.svg" /></a>
                        <ul class="nav">
                            <li>
                                <a href="/">Dashboard</a>
                            </li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Report</a>
                                
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="#">Manage Reports</a>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="/">Configure</a>
                            </li>
                            <li>
                                <a href="/">Manage</a>
                            </li>
                            <li>
                                <a href="/">Development</a>
                            </li>
                            <li>
                                <a href="/">Support</a>
                            </li>
                        </ul>
                        <ul class="nav pull-right">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Carey Hinoki</a>
                                
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="#">Account</a>
                                    </li>
                                    <li>
                                        <a href="#">Logout</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                        <form class="navbar-search pull-right">
                            <input type="text" class="search-query" placeholder="Search" />
                        </form>
                    </nav>
                </div>
            </div>
        </header>
        
        <div id="main" role="main">
            <decorator:body />
        </div>
        
        <footer></footer>

        <script src="v7/js/script.js"></script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            var _gaq=[['_setAccount','UA-2785572-4'],['_trackPageview']];
            (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
            g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
            s.parentNode.insertBefore(g,s)}(document,'script'));
        </script>
    </body>
</html>
