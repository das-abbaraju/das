<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Question</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
<tr><td style="vertical-align: top">

<s:form id="save">
<s:hidden name="id" />
<s:hidden name="question.subCategory.id" />
<table class="forms">
<tr>
<th>ID:</th>
<td><s:property value="question.questionID"></s:property></td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="question.number" size="4"/></td>
</tr>
<tr>
<th>Question:</th>
<td><s:textarea name="question.question" rows="2" cols="50" /></td>
</tr>
<tr>
<th>Added:</th>
<td><s:date name="question.dateCreated" /></td>
</tr>
<tr>
<th>Updated:</th>
<td><s:date name="question.lastModified" /></td>
</tr>
<tr>
<th>Has Requirement:</th>
<td><s:checkbox name="question.hasRequirement" value="question.hasRequirement.name() == 'Yes' ? true : false"/></td>
</tr>
<tr>
<th>OK Answer:</th>
<td><s:textfield name="question.okAnswer" /></td>
</tr>
<tr>
<th>Requirement:</th>
<td><s:textarea name="question.requirement" rows="2" cols="50" /></td>
</tr>
<tr>
<th>Flaggable:</th>
<td><s:checkbox name="question.isRedFlagQuestion" value="question.isRedFlagQuestion.name() == 'Yes' ? true : false"/></td>
</tr>
<tr>
<th>Required:</th>
<td><s:select list="#{'No':'No','Yes':'Yes','Depends':'Depends'}" name="question.isRequired" /></td>
</tr>
<tr>
<th>Depends on Question ID:</th>
<td><s:textfield name="question.dependsOnQuestion.questionID" /></td>
</tr>
<tr>
<th>Depends on Answer:</th>
<td><s:textfield name="question.dependsOnAnswer" /></td>
</tr>
<tr>
<th>Question Type:</th>
<td><s:select list="questionTypes" name="question.questionType" /></td>
</tr>
<tr>
<th>Title:</th>
<td><s:textfield name="question.title" /></td>
</tr>

<tr>
<th>Visible:</th>
<td><s:checkbox name="question.isVisible"  value="question.isVisible.name() == 'Yes' ? true : false"/></td>
</tr>
<tr>
<th>Grouped with Previous:</th>
<td><s:checkbox name="question.isGroupedWithPrevious"  value="question.isGroupedWithPrevious.name() == 'Yes' ? true : false"/></td>
</tr>

<tr>
<th>Url 1:</th>
<td><s:textfield name="question.linkUrl1" /></td>
</tr>
<tr>
<th>Label 1:</th>
<td><s:textfield name="question.linkText1" /></td>
</tr>
<tr>
<th>Url 2:</th>
<td><s:textfield name="question.linkUrl2" /></td>
</tr>
<tr>
<th>Label 2:</th>
<td><s:textfield name="question.linkText2" /></td>
</tr>
<tr>
<th>Url 3:</th>
<td><s:textfield name="question.linkUrl3" /></td>
</tr>
<tr>
<th>Label 3:</th>
<td><s:textfield name="question.linkText3" /></td>
</tr>
<tr>
<th>Url 4:</th>
<td><s:textfield name="question.linkUrl4" /></td>
</tr>
<tr>
<th>Label 4:</th>
<td><s:textfield name="question.linkText4" /></td>
</tr>
<tr>
<th>Url 5:</th>
<td><s:textfield name="question.linkUrl5" /></td>
</tr>
<tr>
<th>Label 5:</th>
<td><s:textfield name="question.linkText5" /></td>
</tr>
<tr>
<th>Url 6:</th>
<td><s:textfield name="question.linkUrl6" /></td>
</tr>
<tr>
<th>Label 6:</th>
<td><s:textfield name="question.linkText6" /></td>
</tr>
</table>
<button class="positive" name="button" value="save">Save</button>
<button class="positive" name="button" value="delete">Delete</button>
</s:form>
</td>
<td style="vertical-align: top">
	<table class="report">
	</table>
</td>
</tr>
</table>

</body>

</html>