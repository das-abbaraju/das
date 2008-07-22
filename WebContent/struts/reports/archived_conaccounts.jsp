<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Delinquent Contractor Accounts</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Delinquent Contractor Accounts</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These contractors have allowed their PICS membership to lapse. If you expect to do additional work
with any of these contractors, please encourage them to renew their membership by contacting PICS.
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Invoice Date</td>
		<td>Contact</td>
		<td>Phone Number</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="[0].get('name')" /></td>
			<td class="center"><s:date name="[0].get('lastInvoiceDate')" format="M/d/yy" /></td>
			<td><s:property value="[0].get('contact')" /></td>
			<td><s:property value="[0].get('phone')" /><br/>
			<s:property value="[0].get('phone2')" />
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
