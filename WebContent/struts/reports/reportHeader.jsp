<%@ taglib prefix="s" uri="/struts-tags"%>
<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
%>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/Search.js?v=<s:property value="version"/>"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="js/jquery/jquery.hoverIntent.js"></script>
<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>

<script type="text/javascript">
$(document).ready(function() {
	wireClueTips();
	$('.datepicker').datepicker();
	$('.dropmenudiv').bgiframe();
});
</script>