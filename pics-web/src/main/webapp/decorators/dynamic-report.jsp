<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="app_dir">v7/js/extjs/pics</s:set>
<s:set var="css_dir">v7/js/extjs/pics/resources/css</s:set>
<s:set var="extjs_dir">v7/js/extjs/pics/extjs</s:set>

<s:set var="is_development_mode" value="false" />

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">

		<title>PICS - <decorator:title default="PICS" /></title>

        <link rel="stylesheet" href="/bootstrap3/css/style.css?v=${version}">
        <!--[if lt IE 8]><link rel="stylesheet" href="/bootstrap3/css/vendor/font-awesome-ie7.css"><![endif]-->

        <%--Make ie8 corrections asap--%>
        <!--[if lt IE 9]>
            <script src="/bootstrap3/js/vendor/respond.js?v=${version}"></script>
            <link rel="stylesheet" href="/bootstrap3/css/ie8.css?v=${version}">
        <![endif]-->

        <%-- include javascript translations --%>
        <s:action name="TranslateJS2" executeResult="true" />

        <%-- Translations: globally scoped and integrated into the application --%>

        <%-- hella balls hacky, placing text method on the global PICS namespace, Ext.create.Application will add this to the scope it creates --%>
        <%-- this is emulated in index.html, the file that is used to compile the application --%>
        <script>
        PICS.text = function (key, escape) {
            var args = arguments,
                translation;

            translation = PICS.i18n[key] ? PICS.i18n[key].replace(/{([0-9]+)}/g, function (match, p1) {
                return args[parseInt(p1) + 1];
            }) : key;

            return typeof escape == 'boolean' && escape ? translation.replace(/'/g, "\\'") : translation;
        };
        </script>

        <s:if test="#is_development_mode == true">
            <script type="text/javascript" src="${extjs_dir}/ext-debug.js"></script>
            <script type="text/javascript" src="${extjs_dir}/ext-debug-override.js"></script>
            <script type="text/javascript" src="${app_dir}/ext-overrides.js"></script>
            <script type="text/javascript" src="${app_dir}/app.js?v=${version}"></script>
        </s:if>
        <s:else>
            <script type="text/javascript" src="${extjs_dir}/ext.js?v=${version}"></script>
            <script type="text/javascript" src="${extjs_dir}/app-all.js?v=${version}"></script>
        </s:else>

        <s:if test="#is_development_mode == true">
            <link rel="stylesheet" type="text/css" href="${css_dir}/my-ext-theme.css?v=${version}" />
        </s:if>
        <s:else>
            <link rel="stylesheet" type="text/css" href="${css_dir}/my-ext-theme.min.css?v=${version}" />
        </s:else>

		<decorator:head />
	</head>
	<body id="${actionName}_${methodName}_page" class="${actionName}-page page" style="margin-top: 50px;">
        <s:action name="Menu!bootstrap3Menu" executeResult="true" />
        <decorator:body />

        <script src="/bootstrap3/js/script.js?v=${version}"></script>

        <s:if test="isBetaEnvironment() || isStableEnvironment()">
            <script type="text/javascript">
                var _gaq = _gaq || [];
                _gaq.push(['_setAccount', 'UA-2785572-4']);
                _gaq.push(['_trackPageview']);

                (function () {
                    var ga = document.createElement('script');
                    ga.type = 'text/javascript';
                    ga.async = true;
                    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                    var s = document.getElementsByTagName('script')[0];
                    s.parentNode.insertBefore(ga, s);
                })();
            </script>
        </s:if>

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
