<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>

<s:include value="userfilters.jsp" />

<div id="report_data">
<s:include value="report_users_data.jsp"></s:include>
</div>

</body>
</html>
