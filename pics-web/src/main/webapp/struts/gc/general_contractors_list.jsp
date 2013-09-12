<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title><s:text name="GeneralContractorList.title" /></title>
	<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
	<h1><s:text name="GeneralContractorList.title" /></h1>
	
	<s:include value="../reports/filters.jsp" />
	
	<div id="report_data">
		<s:include value="general_contractors_list_data.jsp" />
	</div>
</body>
