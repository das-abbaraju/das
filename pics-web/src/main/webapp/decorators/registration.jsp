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
		<link rel="stylesheet" type="text/css" media="screen" href="css/layout.css?v=${version}" />
		<!--[if !IE 6]><!--><link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=${version}" /><!--<![endif]-->
		<link rel="stylesheet" type="text/css" media="screen" href="css/form.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/environment.css?v=${version}" />

		<link rel="stylesheet" type="text/css" media="screen" href="css/registration.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="bootstrap3/css/vendor/select2/select2.css?v=${version}" />
        <link rel="stylesheet" type="text/css" href="bootstrap3/css/vendor/select2/select2-override.css?v=${version}"/>
        <link rel="stylesheet" type="text/css" href="css/timezone.css?v=${version}"/>

        <link rel="stylesheet" type="text/css" href="css/bootstrap/css/font-awesome.min.css?v=${version}"/>
        <!--[if lt IE 8]>
        <link rel="stylesheet" href="css/bootstrap/css/font-awesome-ie7.min.css"><![endif]-->

		<!-- CSS FIXES FOR INTERNET EXPLORER -->
		<!--[if IE]><link rel="stylesheet" href="css/ie.css?v=${version}" type="text/css" /><![endif]-->
		<!--[if IE 6]><link rel="stylesheet" href="css/ie6.css?v=${version}" type="text/css" /><![endif]-->
		<!--[if IE 7]><link rel="stylesheet" href="css/ie7.css?v=${version}" type="text/css" /><![endif]-->

		<!-- JS FIXES FOR INTERNET EXPLORER -->
		<!--[if lt IE 9]><script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	</head>
    <body id="${actionName}_${methodName}_page" class="${actionName}-page page">
        <s:include value="/struts/layout/_environment.jsp" />

		<div id="container">
			<s:include value="/struts/layout/header_registration.jsp" />

			<div id="main" role="main">
				<decorator:body />
			</div>

			<s:include value="/struts/layout/footer_registration.jsp" />
		</div>

		<s:include value="/struts/layout/include_javascript.jsp" />

        <script type="text/javascript">
          var _gaq = _gaq || [];
          _gaq.push(['_setAccount', 'UA-2785572-4']);
          _gaq.push(['_trackPageview']);

          (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
          })();
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