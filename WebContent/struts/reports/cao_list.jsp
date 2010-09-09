<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Audit List with Status</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="cao_list_data.jsp"></s:include>
</div>

</body>
</html>
