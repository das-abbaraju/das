<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportFatalities.title" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<h1><s:text name="ReportFatalities.title" /></h1>
<s:include value="filters.jsp" />

<div id="report_data">
	<s:include value="report_fatalities_data.jsp"></s:include>
</div>

</body>
</html>
