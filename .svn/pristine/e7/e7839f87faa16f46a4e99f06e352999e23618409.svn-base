<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>
    <tiles:insertAttribute name="title" ignore="true" />
</title>
<tiles:importAttribute ignore="true" name="style" />
<tiles:importAttribute ignore="true" name="script" />
<link rel="stylesheet" type="text/css" href="static/global/css/reset.css" />
<link rel="stylesheet" type="text/css" href="static/global/css/fonts.css" />
<link rel="stylesheet" type="text/css" href="static/global/css/global.css" />
<link rel="stylesheet" type="text/css" href="static/global/css/jquery-ui-1.10.4.custom.min.css" />
<link rel="stylesheet" type="text/css" href="static/global/plugins/jquery-ui-1.10.4.custom/css/ui-darkness/jquery-ui-1.10.4.custom.min.css" />
<!--
page specific css
-->
 <link rel="stylesheet" type="text/css" href="${style}" />

<script src="static/global/js/jquery-1.11.0.min.js" type="text/javascript" > </script>
<script src="static/global/plugins/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js" type="text/javascript" > </script>
<script src="static/global/js/handlebars.1.3.0.js" type="text/javascript" > </script>
<script src="static/global/js/global.js" type="text/javascript" type="text/javascript"> </script>
<script src="static/global/plugins/jquery-validate/validate.js" type="text/javascript"></script>
<script src="static/global/plugins/jquery-validate/methods.js" type="text/javascript"></script>
<script src="static/global/plugins/jquery.simplemodal.1.4.4.min.js" type="text/javascript"></script>
<script src="static/global/plugins/spin.min.js" type="text/javascript"></script>
<script src="static/global/helpers/global-helpers.js" type="text/javascript"></script>
<!--
page specific js
-->
<script type="text/javascript" src="${script}"></script>
<script type="text/javascript" src="static/global/events/global-events.js"></script>

</head>
<body>
<div id="main-container">
    <div id="wrapper">
        <div id="body-wrapper">
            <div id="my-account-container">
                <div id="side-nav" class="floatLeft">
                   <tiles:insertAttribute name="sidenav" />
                </div>
                <div id="content-section" class="floatLeft">
                    <tiles:insertAttribute name="header" />
                    <tiles:insertAttribute name="body" />
                </div>
            </div>

        </div>
        <div id="footer-wrapper"></div>
    </div>
</div>

</body>
</html>
