<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<% String version = com.picsauditing.util.AppVersion.current.getVersion(); %>
<html>
<head>
  <title><s:text name="ContractorPricing.title"></s:text></title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<%=version%>" />
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" style="margin: 10px; text-align: center;">
<%@ include file="includes/pricing_matrix.jsp" %>
</body>
</html>
