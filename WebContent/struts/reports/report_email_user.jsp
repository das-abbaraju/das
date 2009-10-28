<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Email Subscription</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Email Subscription</h1>

<s:form id="form1">
	<s:hidden name="filter.ajax" value="false"/>
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=a.name" >Account</a></th>
	    <th>UserName</th>
	    <th>Subscription</th>
		<td>Time Period</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="[0].get('name')"/></td>
			<td><s:property value="get('username')"/></td>
			<td class="right"><s:property value="get('subscription')"/></td>
			<td><s:property value="get('timePeriod')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>


</body>
</html>
