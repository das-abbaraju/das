<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Policy List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Policy List</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="policy_list_data.jsp"></s:include>
</div>

</body>
</html>
