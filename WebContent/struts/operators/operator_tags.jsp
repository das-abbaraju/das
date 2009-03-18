<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operatorAccount.name" /> Tags</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<h1><s:property value="operatorAccount.name" /> Tags</h1>

<s:iterator value="tags">
	<s:property value="id" />
	<s:property value="tag" />
	<s:property value="active" />
	<br />
</s:iterator>

</body>
</html>
