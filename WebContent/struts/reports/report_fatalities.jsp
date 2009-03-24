<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Fatalities</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Fatalities Report</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="?orderBy=a.name">Contractor</a></th>
			<s:if test="permissions.operator">
				<td><a href="?orderBy=flag DESC">Flag</a></td>
			</s:if>
			<td><a href="?orderBy=ca.auditFor DESC">For</a></td>
			<td>Fatalities</td>
			<s:if test="showContact">
				<td>Primary Contact</td>
				<td>Phone</td>
				<td>Phone2</td>
				<td>Email</td>
				<td>Office Address</td>
				<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
				<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
				<td>Zip</td>
				<td>Second Contact</td>
				<td>Second Phone</td>
				<td>Second Email</td>
				<td>Web_URL</td>
			</s:if>
			<s:if test="showTrade">
				<td>Trade</td>
				<td>Industry</td>			
			</s:if>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<s:if test="permissions.operator">
				<td class="center"><a
					href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>"
					title="<s:property value="[0].get('flag')"/> - Click to view details"><img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a></td>
			</s:if>
			<td class="center"><s:property value="get('auditFor')" /></td>
			<td class="center"><s:property value="get('fatalities')" /></td>
			<s:if test="showContact">
				<td><s:property value="get('contact')"/></td>
				<td><s:property value="get('phone')"/></td>
				<td><s:property value="get('phone2')"/></td>
				<td><s:property value="get('email')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('secondContact')"/></td>
				<td><s:property value="get('secondPhone')"/></td>
				<td><s:property value="get('secondEmail')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
				<td><s:property value="get('industry')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<br>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>
