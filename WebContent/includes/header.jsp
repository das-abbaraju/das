<html>
<head>
	<title><%=pageBean.getTitle() %></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<%	if (!pageBean.isCached()) { %>
	<META Http-Equiv="Cache-Control" Content="no-cache">
	<META Http-Equiv="Pragma" Content="no-cache">
	<META Http-Equiv="Expires" Content="0">
	<%	} %>
	<%	if (pageBean.includePrototype()) { %>
	<script src="js/prototype.js" type="text/javascript"></script>
	<%	} %>
	<%	if (pageBean.includeScriptaculous()) { %>
	<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
	<%	} %>
	<link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0"
	topmargin="0" marginwidth="0" marginheight="0">
