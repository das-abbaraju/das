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
.selected {
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
table.legend {
	clear: both;
	margin: 5px 0px;
}
table.legend td {
	padding: 3px;
	vertical-align: middle;
}
div.box {
	width: 20px;
	height: 20px;
	border: 1px solid #012142;
}
</style>
<s:include value="../jquery.jsp" />
</head>
<body>
<s:if test="auditID > 0">
	<div class="info"><a href="Audit.action?auditID=<s:property value="auditID" />">Return to Job Roles Self Assessment</a></div>
</s:if>
<h1><s:property value="account.name" /><span class="sub"><s:property value="subHeading" escape="false" /></span></h1>
<div class="right">
	<a class="excel" href="JobCompetencyMatrix.action?button=Download&id=<s:property value="id" />" target="_BLANK" 
		title="Download all <s:property value="competencies.size()"/> results to a CSV file">Download</a>
</div>
<s:if test="permissions.contractor || permissions.admin">
	<a href="ManageJobRoles.action?id=<s:property value="id" />">Manage Job Roles</a><br />
</s:if>

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
					<td><s:property value="#competency.label" /></td>
					<td><img src="images/help.gif" alt="<s:property value="#competency.label" />" title="<s:property value="#competency.category" />: <s:property value="#competency.description" />" /></td>
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

<table class="legend">
	<tr>
		<td><div class="box selected"><img alt="X" src="images/checkBoxTrue.gif"></div></td>
		<td>The competency is required for the above job task</td>
	</tr>
</table>
</body>
</html>