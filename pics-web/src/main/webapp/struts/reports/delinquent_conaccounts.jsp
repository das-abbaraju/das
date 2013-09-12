<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="DelinquentContractorAccounts.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="DelinquentContractorAccounts.title" /></h1>

<s:include value="filters.jsp" />
<div id="report_data">
<s:include value="delinquent_conaccounts_data.jsp"></s:include>
</div>

</body>
</html>
