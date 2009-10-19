<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />

<script type="text/javascript" src="js/jquery/jquery.hoverIntent.js"></script>
<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.js"></script>
<link type="text/css" rel="stylesheet" href="js/jquery/cluetip/jquery.cluetip.css" />
<script type="text/javascript">
$(document).ready(function() {
	$("a.contractorQuick").cluetip({
		sticky: true, 
		hoverClass: 'cluetip', 
		clickThrough: true, 
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		hoverIntent: {interval: 200},
		arrows: true,
		dropShadow: false,
		width: 400,
		cluetipClass: 'jtip',
		ajaxProcess:      function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
});
</script>

</head>
<body>
<h1><s:property value="reportName" /></h1>

<div id="report_data">
<s:include value="report_billing_data.jsp"></s:include>
</div>

<div id="reportFilters" style="padding: 0; margin: 0; height: 100%; z-index:10000;">
<a class="handle" href="#">Filter Report</a>
<div class="content" style="height: 100%; padding: 0; margin: 0; overflow-y: scroll; overflow-x: hidden;">
<div style="text-align: center;">
<br><br><br>
<h4>Loading Report Filters</h4>
<img src='images/ajax_process2.gif' />
</div>
</div>
</div>

</body>
</html>
