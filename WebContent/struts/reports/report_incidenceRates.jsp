<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Incidence Rates</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Incidence Rates Report</h1>
<s:include value="filters.jsp" />

<div id="report_data">
	<s:include value="report_incidenceRates_data.jsp"></s:include>
</div>

</body>
</html>