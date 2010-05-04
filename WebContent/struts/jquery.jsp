<%@ taglib prefix="s" uri="/struts-tags"%>
<% 
// Usage: <s:include value="../jquery.jsp" />
// Use to include jQuery, jQuery UI, and Gritter (for notifications)
// If you just need jQuery, the just include it with the single line
%>

<script type="text/javascript" src="<s:property value="protocol"/>://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="<s:property value="protocol"/>://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>

<script type="text/javascript" src="js/jquery/util/jquery-utils.js"></script>
<script type="text/javascript" src="js/jquery/gritter/jquery.gritter.js"></script>

<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css"/>

<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css">
<link rel="stylesheet" type="text/css" href="js/jquery/gritter/css/gritter.css" />

<script type="text/javascript">
	$(document).ready(function() {
	    if($.browser.mozilla) 
	         $("form").attr("autocomplete", "off");

        if($.browser.msie && $.browser.version == '6.0'){
            try {
	        	var xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
				$('#content').prepend($('<div/>').addClass('error').text('ActiveX is required for PICS to function properly in your browser. Please Contact your IT Department.'));
            }
        }
	});
</script>