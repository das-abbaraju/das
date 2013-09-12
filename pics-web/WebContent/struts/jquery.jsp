<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%@ page import="com.picsauditing.model.i18n.LanguageModel" %>
<%@ page import="com.picsauditing.util.SpringUtils" %>
<%@ page import="java.util.Locale" %>

<%--
	Usage: <s:include value="../jquery.jsp" />
	Use to include jQuery, jQuery UI, and Gritter (for notifications)
	If you just need jQuery, the just include it with the single line
--%>

<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css?v=${version}">
<link rel="stylesheet" type="text/css" href="js/jquery/gritter/css/gritter.css?v=${version}" />
<link rel="stylesheet" type="text/css" href="js/jquery/facebox/facebox.css?v=${version}" media="screen" />
<link type="text/css" rel="stylesheet" href="js/jquery/cluetip/jquery.cluetip.css?v=${version}" media="screen">

<%
    if (!TranslationActionSupport.getLocaleStatic().getLanguage().equals("en")) {
        Locale locale = TranslationActionSupport.getLocaleStatic();
        LanguageModel languageModel = (LanguageModel) SpringUtils.getBean("LanguageModel");
        // TODO Find a way to avoid using SpringUtils
%>
<script type="text/javascript" src="js/jquery/jquery-ui/i18n/jquery.ui.datepicker-<%= languageModel.getClosestVisibleLocale(locale).getLanguage() %>.js?v=${version}"></script>
<% } %>

<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/gritter/jquery.gritter.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js?v=${version}"></script>

<script type="text/javascript">
	$(function() {
		$.ajaxSettings.traditional = true;

		if ($.browser.mozilla) {
			$("form:not(#login)").attr("autocomplete", "off");
		}

		if ($.browser.msie && $.browser.version == '6.0') {
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
			
			$('input[type=text],input[type=password],select,textarea', 'fieldset.form ol').addClass('input-edit-field');
		}
		
		$('.hoverable').live('mouseenter', function() {
   			$(this).addClass('hover');
   		}).live('mouseleave', function() {
   			$(this).removeClass('hover');
 		});
	});
</script>