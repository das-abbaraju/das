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
		<th>CC</th>
		<th>Invoice #</th>
		<th>Invoice Total</th>
		<th><a href="javascript: changeOrderBy('form1','dueDate');">Due Date</a></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td class="center"><s:if test="ccOnFile">Yes</s:if><s:else>No</s:else></td>
			<td class="center"><a href="InvoiceDetail.action?invoice.id=<s:property value="get('invoiceId')"/>"><s:property value="get('invoiceId')"/></a></td>
			<td class="right">$<s:property value="get('totalAmount')"/></td>
			<td class="right"><s:date name="get('dueDate')" format="M/d/yy"/></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
