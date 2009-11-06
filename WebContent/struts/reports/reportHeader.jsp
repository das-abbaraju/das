<%@ taglib prefix="s" uri="/struts-tags"%>
<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
%>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/Search.js?v=20091105"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />

<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>

<script type="text/javascript">
$(document).ready(function() {
	wireClueTips();
	$('.datepicker').datepicker();
});
</script>