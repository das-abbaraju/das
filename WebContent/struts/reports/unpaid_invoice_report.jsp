<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
	    <th>Edit</th>
		<th class="center"><a href="javascript: changeOrderBy('form1','ccOnFile DESC');">CC</a></th>
		<th><a href="javascript: changeOrderBy('form1','i.id');">Invoice #</a></th>
		<th><a href="javascript: changeOrderBy('form1','totalAmount DESC');">Invoice Total</a></th>
		<th><a href="javascript: changeOrderBy('form1','i.creationDate');">Invoiced</a></th>
		<th><a href="javascript: changeOrderBy('form1','dueDate');">Due Date</a></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td><a href="BillingDetail.action?id=<s:property value="get('id')"/>" target="BILLING_DETAIL">Billing Detail</a></td>
			<td class="center"><s:if test="get('ccOnFile')">Yes</s:if><s:else>No</s:else></td>
			<td class="center"><a href="InvoiceDetail.action?invoice.id=<s:property value="get('invoiceId')"/>"><s:property value="get('invoiceId')"/></a></td>
			<td class="right">$<s:property value="get('totalAmount')"/></td>
			<td class="right"><s:date name="get('invoicedDate')" format="M/d/yy"/></td>
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
