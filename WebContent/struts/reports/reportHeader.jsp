<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
%>
<%
// Usage: <s:include value="../jquery.jsp" />
// Use to include jQuery, jQuery UI, and Gritter (for notifications)
// If you just need jQuery, the just include it with the single line
%>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.picsReport.js"></script>
<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

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
