<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Delinquent Contractor Accounts</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Delinquent Contractor Accounts</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These contractors will be deactivated from the PICS system in the next few days. If you expect to do 
work with any of these contractors, please encourage them to renew their 
membership by contacting PICS.
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Invoice Date</td>
		<td>Days Left</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
			<td class="center"><s:date name="[0].get('lastInvoiceDate')" format="M/d/yy" /></td>
			<td class="center"><s:property value="[0].get('DaysLeft')" /></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
