<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportContractorOperatorFlag.subheading" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName"/></h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="report_contractorOperatorFlag_data.jsp"></s:include>
</div>

</body>
</html>
