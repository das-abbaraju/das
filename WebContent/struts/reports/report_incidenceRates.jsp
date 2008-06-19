<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Incidence Rates</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
</head>
<body>
<h1>Incidence Rates Report</h1>
<s:form id="form1" name="form1" method="post">
	<table border="0" cellpadding="2" cellspacing="0">
		<tr class="blueMain">
<%//		TODO: Add functionality back in to be a Multi-Select Box %>
<%//			<s:if test="%{value = permissions.corporate}">%>
<%//				<td><s:select list="operatorListWithCorporate" cssClass="forms"%>
<%//					name="operator" listKey="id" listValue="name" />&nbsp;</td>%>
<%//			</s:if>%>
			<td align="right">Incidence Rate Cutoff:</td>
			<td><s:textfield name="incidenceRate" size="5" />&nbsp;</td>
			<td><s:submit name="imageField" type="image"
				src="images/button_search.gif" onclick="runSearch('form1')" /></td>
		</tr>
		<s:hidden name="showPage" value="1" />
		<s:hidden name="startsWith" />
		<s:hidden name="orderBy" />
	</table>
	<div><strong>Check next to the years to search</strong></div>
	<br>
	<br>
	<div><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<th><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></th>
				<td>Location</td>
				<td>Type</td>
				<td><s:property value="year-1" /><s:checkbox
					name="searchYear1" /></td>
				<td><s:property value="year-2" /><s:checkbox
					name="searchYear2" /></td>
				<td><s:property value="year-3" /><s:checkbox
					name="searchYear3" /></td>
			</tr>
		</thead>
		<!--TODO Add in the Contractor FlagColor-->
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a
					href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property
					value="[0].get('name')" /></a></td>
				<td><s:if test="%{[0].get('location') == 'Corporate'}">
					<s:property value="[0].get('location')" />
				</s:if> <s:else>
					<s:property
						value="%{[0].get('location')+'-'+[0].get('description')}" />
				</s:else></td>
				<td><s:property value="[0].get('SHAType')" /></td>
				<!--Need to fix this before the year end-->
				<td class="right"><s:property
					value="%{new java.text.DecimalFormat('#,##0.00').format([0].get('recordableTotal1')*200000f/[0].get('manHours1'))}" /></td>
				<td class="right"><s:property
					value="%{new java.text.DecimalFormat('#,##0.00').format([0].get('recordableTotal2')*200000f/[0].get('manHours2'))}" /></td>
				<td class="right"><s:property
					value="%{new java.text.DecimalFormat('#,##0.00').format([0].get('recordableTotal3')*200000f/[0].get('manHours3'))}" /></td>
			</tr>
		</s:iterator>
	</table>
	<br>
</s:form>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>