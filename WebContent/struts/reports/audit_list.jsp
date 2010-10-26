<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Document List Compressed</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Document List Compressed</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="audit_list_data.jsp"></s:include>
</div>

</body>
</html>
