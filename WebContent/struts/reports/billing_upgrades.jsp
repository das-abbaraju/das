<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Membership Upgrades</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Membership Upgrades</h1>
<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <th>Edit</th>
	    <th class="center"><a href="javascript: changeOrderBy('form1','payingFacilities DESC');" title="Paying Facilities" >Fac</a></th>
	    <th class="center"><a href="javascript: changeOrderBy('form1','paymentMethodStatus');" title="Valid Credit Card" >CC</a></th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','membershipDate');">Member Since</a></th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','paymentExpires');">Renewal Date</a></th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','lastInvoiceDate DESC');">Last Inv</a></th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','billingAmount DESC');">Mmbrshp Level</a></th>
	    <th class="right">Inv Amt</th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','lastPayment');">Last Pmt</a></th>
	    <th class="right"><a href="javascript: changeOrderBy('form1','newBillingAmount');">New Mmbrshp Level</a></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"
				><s:property value="get('name')" /></a>
			</td>
			<td><a
				href="ContractorEdit.action?id=<s:property value="get('id')"/>"
				>Edit</a></td>
			<td class="center"><s:property value="get('payingFacilities')"/></td>
			<td class="center">
				<s:if test="get('paymentMethod') == 'Check'">$</s:if>
				<s:property value="get('paymentMethodStatus')"/>
			</td>
			<td><s:property value="get('membershipDate')"/></td>
			<td><s:property value="get('paymentExpires')"/></td>
			<td><s:property value="get('lastInvoiceDate')"/></td>
			<td><s:property value="get('billingAmount')"/></td>
			<td><s:property value="get('')"/></td>
			<td><s:property value="get('lastPaymentAmount')"/></td>
			<td><s:property value="get('newBillingAmount')"/></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
