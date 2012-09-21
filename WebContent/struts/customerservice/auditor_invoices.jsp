<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Safety Professional Invoices</title>
<link rel="stylesheet" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript">
function getDetail(auditorID, paidDate) {
	startThinking({type: "large", message: "retrieving Invoice Batch"});
	$('#invoicedetail').fadeTo("slow", 0.33);
	$('#audit_detail').load('AuditorInvoicesAjax.action', 
			{button: 'detail', auditorID: auditorID, paidDate: paidDate},
			function() { $('#invoicedetail').fadeIn();}
	);
}
</script>
</head>
<body>
<s:include value="../actionMessages.jsp" />
<div class="left noprint">
	<h4>Click to see Batch</h4>
	<table class="report" style="line-height:12px;">
	<thead>
	<tr>
		<td>Paid</td>
		<td>Safety Professional</td>
		<td>Audits</td>
	</tr>
	</thead>
	<s:iterator value="list">
	<tr class="clickable" onclick="getDetail(<s:property value="get('auditor').id"/>,'<s:date name="get('paidDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>')">
		<td>
			<s:date name="get('paidDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
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
<div id="audit_detail" class="right"><div id="mainThinkingDiv"></div></div>

</body>
</html>