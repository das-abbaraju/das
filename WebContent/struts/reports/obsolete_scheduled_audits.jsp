<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
	<head>
		<title>Obsolete Scheduled Audits</title>
		<s:include value="reportHeader.jsp" />
	</head>
	<body>
		<h1>Obsolete Scheduled Audits</h1>
		
		<s:include value="filters.jsp" />
		
		<div id="report_data">
			<s:include value="obsolete_scheduled_audits_data.jsp"></s:include>
		</div>
	</body>
</html>