<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
	if (!permissions.loginRequired(response, request))
		return;
%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"	prefix="decorator"%>
<html>
<head>
<title>PICS - <decorator:title
	default="Pacific Industrial Contractor Screening" /></title>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link href="PICS.css" rel="stylesheet" type="text/css">
<decorator:head />
<%
	/*
	 <script src="js/prototype.js" type="text/javascript"></script>
	 <script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
	 <script src="js/Search.js" type="text/javascript"></script>
	 */
%>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr height="145">
		<td valign="top">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="50%" bgcolor="#993300">&nbsp;</td>
				<td width="146" valign="top" rowspan="2"><a href="index.jsp"><img
					src="images/logo.gif" alt="HOME" width="146" height="145"
					border="0"></a></td>
				<td width="364"><%@ include
					file="../utilities/mainNavigation.jsp"%></td>
				<td width="147"><img src="images/squares_rightUpperNav.gif"
					width="147" height="72" border="0"></td>
				<td width="50%" bgcolor="#993300">&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td valign="top" align="center"><img
					src="images/<decorator:getProperty property="meta.header_gif" default="header_reports.gif" />"
					height="72"></td>
				<td valign="top"><%@ include
					file="../utilities/rightLowerNav.jsp"%></td>
				<td>&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td align="center" valign="top"><decorator:body /><br /><br /></td>
	</tr>
	<tr bgcolor="#003366">
		<td height="72" align="center" valign="middle" class="footer">&copy;
		Copyright 2008 Pacific Industrial Contractor Screening</td>
	</tr>
</table>
<%@ include file="../includes/statcounter.jsp"%>
</body>
</html>
