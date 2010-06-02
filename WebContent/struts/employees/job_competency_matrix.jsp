<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>HSE Competency Matrix</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />

<style>
table.report td.selected {
	background-color: LightBlue;
	text-align: center;
	font-weight: bold;
	color: #003768;
	padding: 0;
	vertical-align: middle;
}
table.report td.notselected {
	background-color: white;
	padding: 0;
	vertical-align: middle;
}
</style>
<s:include value="../jquery.jsp" />
</head>
<body>
<h1>HSE Competency Matrix <span class="sub"><s:property value="subHeading" escape="false" /></span></h1>
<div class="beta"></div>
<s:if test="permissions.contractor || permissions.admin">
	<a href="ManageJobRoles.action?id=<s:property value="id" />">Manage Job Roles</a><br />
</s:if>
<a class="excel" href="JobCompetencyMatrix.action?button=Download&id=<s:property value="id" />" target="_BLANK" 
	title="Download all <s:property value="competencies.size()"/> results to a CSV file">Download</a>
<table class="report">
	<thead>
		<tr>
			<th colspan="2">HSE Competency</th>
			<s:iterator value="roles">
				<th><s:property value="name" /></th>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="competencies" id="competency">
			<s:if test="getRoles(#competency) != null">
				<tr>
					<td><s:property value="#competency.category" /></td>
					<td><s:property value="#competency.label" /></td>
					<s:iterator value="getRoles(#competency)" id="role">
						<s:if test="getJobCompetency(#role, #competency).id > 0">
							<td class="selected"><img alt="X" src="images/checkBoxTrue.gif"></td>
						</s:if>
						<s:else>
							<td class="notselected"></td>
						</s:else>
					</s:iterator>
				</tr>
			</s:if>
		</s:iterator>
	</tbody>
</table>
</body>
</html>