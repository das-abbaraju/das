<%@ taglib prefix="s" uri="/struts-tags"%>
<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
%>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/Search.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>

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

$(function(){
	$('.datepicker').datepicker();
});
</script>