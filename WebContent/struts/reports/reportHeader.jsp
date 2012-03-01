<%@ taglib prefix="s" uri="/struts-tags" %>

<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
%>

<s:include value="../jquery.jsp"/>

<link rel="stylesheet" type="text/css" href="js/jquery/tokeninput/styles/token-input.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="js/jquery/tokeninput/jquery.tokeninput.js"></script>
<script type="text/javascript" src="js/jquery/jquery.hoverIntent.js"></script>
<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="js/filters.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/ReportSearch.js?v=<s:property value="version"/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
    	wireClueTips();
    	$('.datepicker').datepicker();
    	$('.dropmenudiv').bgiframe();
    });
</script>