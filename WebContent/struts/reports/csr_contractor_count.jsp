<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractors Assigned To CSRs</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractors Assigned To CSRs</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="csr_contractor_count_data.jsp"></s:include>
</div>
</body>
</html>
