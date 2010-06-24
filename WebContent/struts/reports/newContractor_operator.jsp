<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Search for New Contractors</title>
<s:include value="reportHeader.jsp" />
<style type="text/css">
table.report thead a.cluetip {
	color: #FFF;
	text-decoration: none;
	background: url("images/help.gif") no-repeat left center;
	margin-left: 2px;
	padding-left: 5px;
}
</style>
</head>
<body>
<h1>Search for New Contractors</h1>
<s:include value="filters.jsp" />

<div id="report_data">
	<div class="info">
	Search for new companies to add to your database by either company name or trade.
	</div>
</div>

</body>
</html>