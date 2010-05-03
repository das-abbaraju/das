<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>OQ by Contractor/Site</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>OQ by Contractor/Site</h1>

<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Site</th>
			<th>Employees</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="contractorSites">
			<tr>
				<td><s:property value="contractor.name" /></td>
				<td><s:property value="jobSite.name" /></td>
				<td class="right"><a href="ReportOQEmployees.action?conID=<s:property value="contractor.id" />&jobSiteID=<s:property value="jobSite.id" />"><s:property value="totalEmployees" /></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
