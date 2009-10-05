<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Auditor Invoices</title>
<link rel="stylesheet" href="css/reports.css" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript">
function getDetail(auditorID, paidDate) {
	$('#audit_detail').fadeOut().load('AuditorInvoicesAjax.action', 
			{button: 'detail', auditorID: auditorID, paidDate: paidDate},
			function() { $('#audit_detail').fadeIn();}
	);
}
</script>

</head>
<body>
<h1>Auditor Invoices</h1>

<s:include value="../actionMessages.jsp" />

<div id="batch_list" class="left noprint">
<h3>Auditor Batches</h3>
<table class="report">
<thead>
<tr>
	<td>Paid Date</td>
	<td>Auditor</td>
	<td>Audits</td>
</tr>
</thead>
<s:iterator value="list">
<tr class="clickable" onclick="getDetail(<s:property value="get('auditor').id"/>,'<s:date name="get('paidDate')" format="yyyy-MM-dd"/>')">
	<td>
		<s:date name="get('paidDate')" format="MM/dd/yyyy"/>
	</td>
	<td>
		<s:property value="get('auditor').name"/>
	</td>
	<td>
		<s:property value="get('total')"/>
	</td>
</tr>
</s:iterator>
</table>
</div>

<div id="audit_detail" class="left" style="display:none"></div>
</body>
</html>