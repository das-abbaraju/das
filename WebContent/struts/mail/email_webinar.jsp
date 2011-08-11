<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportEmailWebinar.subheading" /></title>
<s:include value="../reports/reportHeader.jsp"/>
</head>
<body>
<h1><s:text name="ReportEmailWebinar.subheading" /></h1>

<s:include value="../reports/filters.jsp" />

<div id="report_data">
<s:include value="email_webinar_data.jsp"></s:include>
</div>
</body>
</html>
