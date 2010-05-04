<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Search for New Contractors</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Search for New Contractors</h1>
<s:include value="filters.jsp" />
<div id="report_data">
<s:include value="newContractor_operator_data.jsp" />
</div>

</body>
</html>