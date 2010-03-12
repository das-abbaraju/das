<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Operator Flag Matrix</title>
<s:include value="reportHeader.jsp"/>
</head>
<body>

<s:include value="filters.jsp"/>

<s:property value="report.getPageLinksWithDynamicForm()" escape="false"/>

<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Overall Color</th>
			<s:iterator value="flagCriteria">
				<th><s:property value="label"/></th>
			</s:iterator>
		</tr>
	</thead>
		<s:iterator value="data">
			<tr>
				<td><s:property value="get('name')"/></td>
				<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@valueOf(get('overallFlag')).smallIcon" escape="false"/></td>
				<s:iterator value="flagCriteria">
					<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@valueOf(get('flag'+id)).smallIcon" escape="false"/></td>
				</s:iterator>
			</tr>
		</s:iterator>
</table>

<s:property value="report.getPageLinksWithDynamicForm()" escape="false"/>

</body>
</html>