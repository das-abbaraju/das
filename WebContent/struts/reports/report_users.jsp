<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>User Search</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>User Search</h1>

<s:include value="userfilters.jsp" />

<div id="report_data">
<s:include value="report_users_data.jsp"></s:include>
</div>

</body>
</html>
