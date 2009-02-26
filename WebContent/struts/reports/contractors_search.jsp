<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor Search <span class="sub">Quick Version</span></h1>

<s:form id="form1" method="post">
	<s:hidden name="filter.accountName"/>
	<s:hidden name="filter.ajax" />
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
		<td>Type</td>
		<td>Name</td>
		<td>City</td>
		<td>State</td>
	</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('type')" /></td>
			<td>
				<s:if test="%{get('type').equals('Contractor')}">
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"
					><s:property value="get('name')" /></a>
				</s:if>
				<s:else>
					<a href="FacilitiesEdit.action?opID=<s:property value="get('id')"/>"
					><s:property value="get('name')" /></a>
				</s:else>
			</td>
			<td>
				<s:property value="get('city')" />
			</td>
			<td>
				<s:property value="get('state')" />
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
