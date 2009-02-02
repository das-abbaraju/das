<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Activations</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor Activations</h1>
<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td class="center">Pay Fac</td>
                <td colspan="2" width="150"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
                <td align="center"><a href="?changed=0&showPage=1&orderBy=payingFacilities DESC" class="whiteTitle">Pay Fac</a></td>
                <td align="center"><a href="?changed=0&showPage=1&orderBy=paymentMethodStatus DESC" class="whiteTitle">CC</a></td>
				<td align="center"><a href="?changed=0&showPage=1&orderBy=membershipDate DESC" class="whiteTitle">Member Since</a></td>
 			    <td align="center"><a href="?changed=0&showPage=1&orderBy=paymentExpires" class="whiteTitle">Expires</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastInvoiceDate DESC" class="whiteTitle"><nobr>Last Inv</nobr></a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=newBillingAmount" class="whiteTitle"><nobr>New Inv</nobr> Level</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastPayment DESC" class="whiteTitle">Last Pmt</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=newBillingAmount" class="whiteTitle"><nobr>New Pmt</nobr> Level</a></td>
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
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
