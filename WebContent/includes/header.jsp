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
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="../utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="../utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3" align="center" class="blueMain">
		  <!-- Start of Main content -->

