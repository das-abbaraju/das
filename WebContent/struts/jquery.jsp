<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<%
	// Usage: <s:include value="../jquery.jsp" />
	// Use to include jQuery, jQuery UI, and Gritter (for notifications)
	// If you just need jQuery, the just include it with the single line
%>
<script type="text/javascript" src="<%= request.isSecure() ? "https" : "http" %>://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>

<script type="text/javascript" src="js/jquery/util/jquery-utils.js"></script>
<script type="text/javascript" src="js/jquery/gritter/jquery.gritter.js"></script>

<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css">
<link rel="stylesheet" type="text/css" href="js/jquery/gritter/css/gritter.css" />

<link rel="stylesheet" type="text/css" href="js/jquery/facebox/facebox.css" media="screen" />
<script type="text/javascript" src="js/jquery/facebox/facebox.js"></script>

<script type="text/javascript" src="js/jquery/jcrop/js/jquery.Jcrop.min.js"></script>
<link rel="stylesheet" type="text/css" href="js/jquery/jcrop/css/jquery.Jcrop.css" media="screen" />

<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js"></script>
<link type="text/css" rel="stylesheet" href="js/jquery/cluetip/jquery.cluetip.css" media="screen">

<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.ajaxSettings.traditional = true;

	    if($.browser.mozilla) 
	         $("form").attr("autocomplete", "off");

        if($.browser.msie && $.browser.version == '6.0'){
            try {
	        	var xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
				$('#content').prepend($('<div/>').addClass('error').text('ActiveX is required for PICS to function properly in your browser. Please Contact your IT Department.'));
            }
        }

        if ($.browser.msie && $.browser.version == '6.0') {
			$('table.report tr').live('mouseenter', function(event) {
				$(this).addClass('tr-hover');
			}).live('mouseleave', function(event) {
				$(this).removeClass('tr-hover');
			});
			$('tr.clickable').live('mouseenter', function(event) {
				$(this).addClass('tr-hover-clickable');
			}).live('mouseleave', function(event) {
				$(this).removeClass('tr-hover-clickable');
			});
			$('fieldset.form ol :input:not(:checkbox):not(:button):not([type=hidden]):not(:radio):not(:submit)').addClass('input-edit-field');
        }
	});
</script>