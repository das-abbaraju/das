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
	$('#editForm').load('ManageQuestionOption!editAjax.action', {optionID: id});
}
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
			<div id="questionOptions-info"></div>
		</td>
		<td style="padding-left: 20px; vertical-align: top;">
			<a href="#" onclick="loadEdit(0); return false;" class="add">Add New Question Option</a>
			<div id="editForm"></div>
		</td>
	</tr>
</table>

</body>
</html>