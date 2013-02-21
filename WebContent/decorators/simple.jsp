<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ page import="com.picsauditing.util.AppVersion"%>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>PICS - <decorator:title default="PICS" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta http-equiv="Cache-Control" content="no-cache" />
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Expires" content="0" />
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reset.css?v=${version}" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<%=AppVersion.current.getVersion()%>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/form.css?v=<%=AppVersion.current.getVersion()%>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=<%=AppVersion.current.getVersion()%>" />
		
		<script type="text/javascript" src="js/pics_main.js?v=<%=AppVersion.current.getVersion()%>"></script>
		
		<style type="text/css">
		#content {
			padding: 15px;
		}
		
		#main {
			margin: 10px;
		}
		</style>
		
		<decorator:head />
	</head>
	<body onload="<decorator:getProperty property="body.onload" />" onunload="<decorator:getProperty property="body.onunload" />">
		<div id="main">
			<div id="bodyholder">
				<div id="content">
					<!-- !begin content -->
					<decorator:body />
					<!-- !end content -->
				</div>
			</div>
		</div>
	</body>
</html>