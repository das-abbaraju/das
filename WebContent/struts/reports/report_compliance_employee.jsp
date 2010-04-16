<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Compliance By Employee Report</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Compliance By Employee Report</h1>

<s:if test="permissions.operatorCorporate">
	<table class="report" id="matrix">
		<thead>
			<tr>
				<th>Contractor</th>
				<th>Employee</th>
				<th>Job Roles</th>
				<th>Compliance</th>
				<th>Missing</th>
			</tr>
		</thead>
		<tbody>
			
		</tbody>
	</table>
</s:if>

</body>
</html>
