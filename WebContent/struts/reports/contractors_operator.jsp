<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor List - Operator</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor List <span class="sub">
<s:if test="permissions.operator">Operator Version</s:if>
<s:else>Corporate Version</s:else></span></h1>

<div>You have <strong><s:property value="contractorCount" /></strong> contractors in your database.</div>

<s:include value="filters.jsp" />
<s:if test="report.allRows > 1">
	<div class="right"><a class="excel" href="javascript: download('ContractorListOperator');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>
</s:if>

<s:include value="contracotrs_list_data"></s:include>

</body>
</html>
