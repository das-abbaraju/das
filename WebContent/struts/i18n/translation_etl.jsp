<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Import/Export Translations</title>
<s:include value="../jquery.jsp"></s:include>
<script type="text/javascript" src="js/jquery/zClip/jquery.zclip.js"></script>
<script type="text/javascript">
function importTranslations() {
	$('#translationsArea').val('');
	$('#translationETL').show();
}

function exportTranslations() {
	$('#translationETL').load('TranslationETLAjax!exportTranslation.action', { startDate: $('#translationDate').val() },
		function(){
			$('#translationETL').show();
		}
	);
}

$(function() {
	$('.datepicker').datepicker();
});
</script>
</head>
<body>
<div id="buttonsDiv">
	<input type="button" class="picsbutton" value="Import" onclick="importTranslations(); $('#buttonsDiv').hide(); return false;" />
	<input type="button" class="picsbutton" value="Export..." onclick="$('#exportDiv').show(); $('#buttonsDiv').hide(); return false;" />
</div>
<div id="exportDiv" style="display: none; clear: both;">
	Start date: <s:textfield name="startDate" cssClass="datepicker" id="translationDate" /><br />
	<input type="button" class="picsbutton positive" value="Export" onclick="exportTranslations(); return false;" />
</div>
<div id="translationETL" style="display: none; clear: both;">
	<s:textarea name="translations" id="translationsArea" cols="60" rows="60" />
</div>
</body>
</html>