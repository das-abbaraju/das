<%
// Usage: <s:include value="../jquery.jsp" />
// Use to include jQuery, jQuery UI, and Gritter (for notifications)
// If you just need jQuery, the just include it with the single line
%>
<script type="text/javascript">
document.write(unescape("%3Cscript src='" + document.location.protocol + 
	"//ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='" + document.location.protocol + 
	"//ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js' type='text/javascript'%3E%3C/script%3E"));
</script>

<script type="text/javascript" src="js/jquery/util/jquery-utils.js"></script>
<script type="text/javascript" src="js/jquery/gritter/jquery.gritter.js"></script>

<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css">
<link rel="stylesheet" type="text/css" href="js/jquery/gritter/css/gritter.css" />