<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Option Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
<s:if test="question != null">
	$(function() {
		$('#breadcrumbs').append('&nbsp;&gt;&nbsp;&nbsp;<a class="blueMain" href="ManageQuestion.action?id=<s:property value="question.id"/>"><s:property value="question.name"/></a>');
	});
</s:if>
function loadEdit(id) {
	$('#editForm').load('ManageOptionType!editAjax.action', {typeID: id});
}
</script>
</head>
<body>
<h1>Manage Option Types</h1>
<s:if test="question != null && question.id > 0"><a href="ManageQuestion.action?id=<s:property value="question.id" />">&lt;&lt; Back to Manage Question</a></s:if>
<table style="width: 100%;">
	<tr>
		<td style="width: 50%;">
			<table class="report">
				<thead>
					<tr>
						<th></th>
						<th>Name</th>
						<th>Radio</th>
						<th>Unique Code</th>
						<th>Options</th>
						<th>Edit</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="all" status="stat">
						<tr id="row_<s:property value="id" />">
							<td><s:property value="#stat.count" /></td>
							<td class="optionName"><s:property value="name" /></td>
							<td class="optionRadio center"><s:if test="radio"><img src="images/okCheck.gif" alt="Edit" /></s:if></td>
							<td class="optionUniqueCode"><s:property value="uniqueCode" /></td>
							<td class="center"><a href="ManageQuestionOption.action?typeID=<s:property value="id"/><s:if test="question != null && question.id > 0">&questionID=<s:property value="question.id" /></s:if>">Manage</a></td>
							<td><a href="#" onclick="loadEdit(<s:property value="id" />); return false;" class="edit"></a></td>
						</tr>
					</s:iterator>
					<s:if test="all.size == 0">
						<tr>
							<td colspan="6">No Option Types found</td>
						</tr>
					</s:if>
				</tbody>
			</table>
		</td>
		<td style="padding-left: 20px; vertical-align: top;">
			<a href="#" onclick="loadEdit(0); return false;" class="add">Add new option type</a>
			<div id="editForm"></div>
		</td>
	</tr>
</table>
</body>
</html>