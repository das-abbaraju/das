<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Employee List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Employee List</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="employee_list_data.jsp"></s:include>
</div>

</body>
</html>
