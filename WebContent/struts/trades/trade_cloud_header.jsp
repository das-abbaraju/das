<%
// List of Javascript and css files needed to create 
// cloud quick clue tips
%>
<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/jquery.hoverIntent.js"></script>
<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />

<script type="text/javascript">
function wireTradeClueTips() {
	$("#trade-cloud a").cluetip({
		sticky: true, 
		hoverClass: 'cluetip', 
		clickThrough: true, 
		ajaxCache: true,
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		hoverIntent: {interval: 200},
		arrows: true,
		dropShadow: false,
		width: 500,
		cluetipClass: 'jtip',
		ajaxProcess:      function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
}

$(document).ready(function() {
	wireTradeClueTips();
});
</script>