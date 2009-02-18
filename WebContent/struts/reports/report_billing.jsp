<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName" /></h1>
<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','oldAmount');">Old Level</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','newAmount');">New Level</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','billingStatus');">State</a></th>	    
		<th class="right"><a href="javascript: changeOrderBy('form1','creationDate');">Registered</a></th>	    
		<th class="right"><a href="javascript: changeOrderBy('form1','lastUpgradeDate');">Upgraded</a></th>	    
		<th class="right"><a href="javascript: changeOrderBy('form1','paymentExpires');">Renews</a></th>	    
	    <th>Edit</th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td class="right">$<s:property value="get('oldAmount')"/></td>
			<td class="right">$<s:property value="get('newAmount')"/></td>
			<td><s:property value="get('billingStatus')"/></td>
			<td class="right"><s:date name="get('creationDate')" format="M/d/yy"/></td>
			<td class="right"><s:date name="get('lastUpgradeDate')" format="M/d/yy"/></td>
			<td class="right"><s:date name="get('paymentExpires')" format="M/d/yy"/></td>
			<td><a href="BillingDetail.action?invoice.id=<s:property value="get('id')"/>" target="BILLING_DETAIL">Billing Detail</a></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
