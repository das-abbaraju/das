<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ContractorList.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="ContractorList.title" /></h1>
<div>
	<s:text name="ContractorList.message.ContractorsInYourDatabase">
		<s:param value="%{contractorCount}" />
	</s:text>
</div>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="contractors_list_data.jsp"></s:include>
</div>

</body>
</html>
