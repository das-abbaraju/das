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

<!--
page specific css
-->
 <link rel="stylesheet" type="text/css" href="${style}" />
 <link rel="stylesheet" href="static/global/css/jquery.fileupload.css">
<script src="static/global/js/jquery-1.11.0.min.js" type="text/javascript" > </script>
<script src="static/global/plugins/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js" type="text/javascript" > </script>
<script src="static/global/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="static/global/js/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script src="static/global/js/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="static/global/js/jquery.fileupload-image.js"></script>
<!-- The File Upload validation plugin -->
<script src="static/global/js/jquery.fileupload-validate.js"></script>


<!--
page specific js
-->
<script type="text/javascript" src="${script}"></script>


</head>
<body>
<div id="main-container">
    <div id="wrapper">
        <div id="body-wrapper">
            <div id="onboard-container" class="container">
                <div id="content-section">
                    <tiles:insertAttribute name="body" />
                </div>
            </div>

        </div>
        <div id="footer-wrapper">
            <tiles:insertAttribute name="footer" />
        </div>
    </div>
</div>

</body>
</html>
