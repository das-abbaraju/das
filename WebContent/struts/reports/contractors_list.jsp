<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor List</h1>

<div>You have <strong><s:property value="contractorCount" /></strong> contractors in your database.</div>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="contractors_list_data.jsp"></s:include>
</div>

</body>
</html>
