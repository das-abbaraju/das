<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>Rules</title></head>
<body>
<s:property value="testBean.greeting"/>
</body>
</html>