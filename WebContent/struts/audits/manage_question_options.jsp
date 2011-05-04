<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Question Options</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<style type="text/css">
.optionNumber {
	background-image: url('js/jquery/dataTables/images/sort_both.png');
	background-repeat: no-repeat;
	background-position: center left;
	cursor: move;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function loadEdit(id) {
	$('#save').show();
	
	if (id > 0) {
		$('#save input[name=\'optionID\']').val(id);
		$('#optionIDField').text(id);
		$('#save input[name=\'option.name\']').val($('tr#row_' + id + ' .optionName').text());
		if ($('tr#row_' + id + ' .optionVisible').is(':empty'))
			$('#save input[name=\'option.visible\']').removeAttr('checked');
		else
			$('#save input[name=\'option.visible\']').attr('checked', true);
		
		$('#save input[name=\'option.number\']').val($('tr#row_' + id + ' .optionNumber').text());
		$('#save input[name=\'option.score\']').val($('tr#row_' + id + ' .optionScore').text());
		$('#save input[name=\'option.uniqueCode\']').val($('tr#row_' + id + ' .optionUniqueCode').text());
	} else {
		$('#save input[name=\'optionID\']').val(id);
		$('#optionIDField').text(id);
		$('#save input[name=\'option.name\']').val('');
		$('#save input[name=\'option.visible\']').removeAttr('checked');
		$('#save input[name=\'option.number\']').val(id);
		$('#save input[name=\'option.score\']').val(id);
		$('#save input[name=\'option.uniqueCode\']').val('');
	}
}

$(function() {
	$('#questionOptions table.report tbody').sortable({
		helper: function(e, tr) {
		  var $originals = tr.children();
		  var $helper = tr.clone();
		  $helper.children().each(function(index) {
			  $(this).width($originals.eq(index).width())
		  });
		  
		  return $helper;
		}
	}).disableSelection();
});
</script>
</head>
<body>
<h1>Manage Question Option<span class="sub"><s:property value="type.name" /></span></h1>
<a href="ManageOptionType.action<s:if test="question != null && question.id > 0">?questionID=<s:property value="question.id" /></s:if>">&lt;&lt; Back to Manage Option Type</a>
<br />

<table style="width: 100%;">
	<tr>
		<td style="width: 50%;">
			<div id="questionOptions">
				<s:include value="manage_question_options_list.jsp" />
			</div>
		</td>
		<td style="padding-left: 20px; vertical-align: top;">
			<a href="#" onclick="loadEdit(0); return false;" class="add">Add New Question Option</a>
			<s:form cssStyle="display: none;" id="save">
				<s:hidden name="optionID" value="%{option == null ? 0 : option.id}" />
				<s:hidden name="typeID" value="%{type.id}" />
				<s:hidden name="questionID" value="%{question == null ? 0 : question.id}" />
				<fieldset class="form">
					<h2 class="formLegend">Question Option</h2>
					<ol>
						<li><label>ID:</label><span id="optionIDField"><s:property value="option.id" /></span></li>
						<li><s:textfield theme="formhelp" name="option.name" /></li>
						<li><s:checkbox theme="formhelp" name="option.visible" /></li>
						<li><s:textfield theme="formhelp" name="option.number" /></li>
						<li><s:textfield theme="formhelp" name="option.score" /></li>
						<li><s:textfield theme="formhelp" name="option.uniqueCode" /></li>
					</ol>
				</fieldset>
				<fieldset class="form submit">
					<s:submit action="ManageQuestionOption!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
					<s:submit action="ManageQuestionOption!delete" cssClass="picsbutton negative" 
						onclick="return confirm('Are you sure you want to delete this question option?');"
						value="%{getText('button.Delete')}" />
				</fieldset>
			</s:form>
		</td>
	</tr>
</table>

</body>
</html>