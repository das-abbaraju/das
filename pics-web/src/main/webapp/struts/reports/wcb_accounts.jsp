<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>WCB Account Numbers</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>WCB Account Numbers</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="wcb_accounts_data.jsp"></s:include>
</div>
</body>
</html>
