<%
// List of Javascript and css files needed to create 
// cloud quick clue tips
%>
<s:include value="../jquery.jsp"/>

<script type="text/javascript">
function wireTradeClueTips() {
	$("#trade-cloud a.trade").cluetip({
		clickThrough: true, 
		ajaxCache: false,
		closeText: "<img src='images/cross.png' width='16' height='16' />",
		hoverIntent: {interval: 200},
		arrows: true,
		dropShadow: false,
		width: 500,
		cluetipClass: 'jtip',
		ajaxProcess: function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
}

$(document).ready(function() {
	wireTradeClueTips();
});
</script>