<%@ page language="java" errorPage="/exception_handler.jsp"%>
<% String version = com.picsauditing.actions.PicsActionSupport.getVersion(); %>
<html>
<head>
  <title>PICS - Contractor Pricing</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<%=version%>" />
  <link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<%=version%>" />
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" style="margin: 10px; text-align: center;">
<%@ include file="includes/pricing_matrix.jsp" %>
<form>
  <input type="button" value="Close" onClick="window.close()">
</form>
</body>
</html>
