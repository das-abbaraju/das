<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Job Competency Matrix</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />
</head>
<body>
<h1>Job Competency Matrix</h1>

<table class="report">
	<thead>
		<tr>
			<th colspan="2">Competency</th>
			<s:iterator value="roles">
				<th><s:property value="name" /></th>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="competencies" id="competency">
			<tr>
				<td><s:property value="#competency.category" /></td>
				<td><s:property value="#competency.label" /></td>
				<s:iterator value="roles" id="role">
					<s:set name="jc" value="getJobCompetency(#role, #competency)" />
					<td><s:checkbox name="foobar" value="#jc != null" /></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
</table>

<s:iterator value="roles" id="role">
	<h3><s:property value="name" /></h3>
	<table class="report">
		<thead>
			<tr>
				<th>Usually</th>
				<th></th>
				<th>Sometimes</th>
				<th></th>
				<th>Rarely</th>
			</tr>
		</thead>
		<tbody>
			<tr style="background-color: white">
				<td><s:select list="competencies" listKey="id"
					listValue="label" size="10" multiple="true" /></td>
				<td><button disabled="disabled">&lt;</button><br /><button disabled="disabled">&gt;</button></td>
				<td><s:select list="competencies" listKey="id"
					listValue="label" size="10" multiple="true" /></td>
				<td><button disabled="disabled">&gt;</button><br /><button disabled="disabled">&lt;</button></td>
				<td><s:select list="competencies" listKey="id"
					listValue="label" size="10" multiple="true" /></td>
			</tr>
		</tbody>
	</table>
</s:iterator>

<s:iterator value="roles" id="role">
	<h3><s:property value="name" /></h3>
	Require Competencies:
	<ul>
	<s:iterator value="competencies">
		<li><s:property value="label" /></li>
	</s:iterator>
	</ul>
</s:iterator>

<s:iterator value="roles" id="role">
	<h3><s:property value="name" /></h3>
	Require Competencies:
	<s:iterator value="competencies">
		<a href="#"><s:property value="label" /></a>, 
	</s:iterator>
</s:iterator>

</body>
</html>