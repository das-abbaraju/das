<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../reportHeader.jsp" />
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:form id="form1">
	<s:hidden name="filter.ajax" value="false" />
	<s:hidden name="filter.destinationAction" value="ManageUnmappedTests" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="orderBy" />
	<s:hidden name="id" />
	
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Imported Test</th>
				<th># of Records</th>
				<th>Add</th>
			</tr>
		</thead>
		<tbody>	
			<s:iterator value="data" status="stat">
				<tr>
					<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
					<td>
						<s:property value="get('qualificationType')" /> - 
						<s:property value="get('qualificationMethod')" /> -
						<s:property value="get('description')" />
					</td>
					<td class="right"><s:property value="get('records')" /></td>
					<td class="center">
						<a href="?id=<s:property value="id" />&button=Add&stageID=<s:property value="get('id')" />" class="add"></a>
					</td>
				</tr>
			</s:iterator>
			<s:if test="data.size() == 0">
				<tr><td colspan="4">No records found</td></tr>
			</s:if>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:form>

</body>
</html>