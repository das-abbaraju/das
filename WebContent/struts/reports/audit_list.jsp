<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Audit List</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:include value="audit_list_data.jsp"></s:include>
</div>

</body>
</html>
