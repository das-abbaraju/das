<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>this is the struts test page</title></head>
<body>
<s:property value="testBean.greeting"/>
</body>
</html>