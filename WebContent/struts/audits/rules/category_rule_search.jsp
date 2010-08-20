<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Category Rules</title>
<s:include value="jquery.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">
</script>
</head>
<body>

<h1>Manage Category Rules</h1>

<div id="search"><s:form id="form1"
	action="%{filter.destinationAction}">
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div>
	<button id="searchfilter" type="submit" name="button" value="Search"
		class="picsbutton positive">Search</button>
	</div>

	<div class="filterOption"><s:textfield name="filter.accountName"
		cssClass="forms" size="18" onfocus="clearText(this)" /></div>
</s:form></div>

<div id="report_data">
<s:include value="category_rule_search_data.jsp"></s:include>
</div>


<br clear="all" />
</body>
</html>
