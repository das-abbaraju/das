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

        <link rel="stylesheet" href="v7/css/style.css?v=${version}">
        <!--[if lt IE 8]><link rel="stylesheet" href="v7/css/vendor/font-awesome-ie7.css?v=${version}"><![endif]-->

        <%--Make ie8 corrections asap--%>
        <!--[if lt IE 9]>
            <script src="/bootstrap3/js/vendor/respond.js?v=${version}"></script>
            <link rel="stylesheet" href="/bootstrap3/css/ie8.css?v=${version}">
        <![endif]-->

        <script src="v7/js/vendor/modernizr-2.6.1.min.js?v=${version}"></script>
    </head>
    <body id="${actionName}_${methodName}_page" class="${actionName}-page page">

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

        <script src="v7/js/script.js?v=${version}"></script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            var _gaq=[['_setAccount','UA-2785572-4'],['_trackPageview']];
            (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
            g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
            s.parentNode.insertBefore(g,s)}(document,'script'));
        </script>

        <script type="text/javascript">
        (function(w, d) { var a = function() { var a = d.createElement('script'); a.type = 'text/javascript';
            a.async = 'async'; a.src = '//' + ((w.location.protocol === 'https:') ? 's3.amazonaws.com/cdx-radar/' :
            'radar.cedexis.com/') + '01-13504-radar10.min.js'; d.body.appendChild(a); };
            if (w.addEventListener) { w.addEventListener('load', a, false); }
            else if (w.attachEvent) { w.attachEvent('onload', a); }
        }(window, document));
        </script>
    </body>
</html>