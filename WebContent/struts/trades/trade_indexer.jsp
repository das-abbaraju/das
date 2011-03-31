<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Indexer</title>
</head>
<body>

<s:form>
	<s:submit action="ServiceIndexer!index"></s:submit>
</s:form>

</body>
</html>
