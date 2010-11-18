<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<s:iterator value="categories">
	<s:set var="category" value="key" scope="action"/>
</s:iterator>
<html>
<head>
<title>Preview of <s:property value="#category.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>
<h1><s:property value="#category.name"/> Preview</h1>
	<s:include value="audit_cat_view.jsp"/>
</body>
</html>