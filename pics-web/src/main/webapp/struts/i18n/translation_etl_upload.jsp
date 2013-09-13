<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Import/Export Translations</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
	<s:include value="../actionMessages.jsp" />
	<s:form enctype="multipart/form-data" method="post">
		<s:include value="translation_etl_table.jsp" />
	</s:form>
</body>
</html>