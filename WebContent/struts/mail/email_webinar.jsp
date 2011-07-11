<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Max Value Webinar</title>
<s:include value="../reports/reportHeader.jsp"/>
</head>
<body>
<h1>Max Value Webinar</h1>

<s:include value="../reports/filters.jsp" />

<div id="report_data">
<s:include value="email_webinar_data.jsp"></s:include>
</div>
</body>
</html>
