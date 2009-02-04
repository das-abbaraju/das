<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Report Billing</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Report Billing</h1>
<div id="alert">
This page is still under Development.  Do not use.
</div>
<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','newBillingAmount');">Level</a></th>
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
			<td class="right">$<s:property value="get('newBillingAmount')"/></td>
			<td><s:property value="get('billingStatus')"/></td>
			<td class="right"><s:property value="get('creationDate')"/></td>
			<td class="right"><s:property value="get('lastUpgradeDate')"/></td>
			<td class="right"><s:property value="get('paymentExpires')"/></td>
			<td><a href="BillingDetail.action?id=<s:property value="get('id')"/> target="BILLING_DETAIL">Billing Detail</a></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
