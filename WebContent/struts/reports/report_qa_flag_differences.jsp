<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="../actionMessages.jsp"></s:include>

<div>
	<h2>Flag Differences High-Level Report</h2>
	<s:set name="data" value="flagAnalyzer.flagDiffData"/>
	<s:include value="report_qa_tablemodel_table.jsp"/>
</div>
<div>
	<h2>Flag Differences CAO Status Report</h2>
	<s:set name="data" value="flagAnalyzer.flagDiffCaoStatus"/>
	<s:include value="report_qa_tablemodel_table.jsp"/>
</div>
</body>
</html>
