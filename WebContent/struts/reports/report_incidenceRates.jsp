<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Incidence Rates</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Incidence Rates Report</h1>
<s:include value="filters.jsp" />

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></th>
			<td>Location</td>
			<td>Type</td>
			<td>Rate</td>
			<td>Year</td>
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
			</s:if><s:else>
				<s:property
					value="%{[0].get('location')+'-'+[0].get('description')}" />
			</s:else></td>
			<td><s:property value="[0].get('SHAType')" /></td>
			<!--Need to fix this before the year end-->
			<td class="right"><s:property
				value="%{new java.text.DecimalFormat('#,##0.00').format([0].get('recordableTotal')*200000f/[0].get('manHours'))}" />
			</td>
			<td><s:property value="get('auditFor')" />
			</td>
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


<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>