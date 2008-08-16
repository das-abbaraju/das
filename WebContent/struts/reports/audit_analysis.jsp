<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit Analysis</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Audit Analysis</h1>

<s:include value="auditanalysisfilters.jsp" />

<table class="report">
	<thead>
		<tr>
			<td>Month</td>
			<td>Created</td>
			<td>Submitted</td>
			<td>Closed</td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td class="right">
				<s:property value="[0].get('label')" />
			</td>
			<td>
				<s:property value="[0].get('createdCount')" />
			</td>
			<td>
				<s:property value="[0].get('completeCount')" />			
			</td>
			<td>
				<s:property value="[0].get('closedCount')" />			
			</td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
