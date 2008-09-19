<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Question Answer Search</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
function getQuestionList(elm) {
		var pars = 'questionName='+ escape($F(elm));
		var myAjax = new Ajax.Updater('selected_question','QuestionSelectAjax.action', 
		{
			method: 'post', 
			parameters: pars
		}
		);
}
</script>
</head>
<body>
<h1>Question Answer Search</h1>

<div id="search">
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;" onsubmit="runSearch( 'form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />

	<s:iterator value="questions" status="stat">
		<div class="filterOption">
			<div class="buttons">
				<s:hidden name="questions[%{#stat.index}].questionID" value="%{questionID}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].criteria" value="%{criteria}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].answer.answer" value="%{answer.answer}"></s:hidden>
				<s:property value="shortQuestion"/> <s:property value="criteria"/> <s:property value="answer.answer"/>
				<button type="submit" name="button" value="<s:property value="questionID"/>">Remove</button>
			</div>
		</div>
	</s:iterator>
	<br clear="all"/>
	<div class="filterOption">
		Select a Question
		<s:textfield cssClass="forms" name="questionSelect" size="35" onchange="getQuestionList(this)"/><br/> 
		<div id="selected_question">&nbsp;</div>
	</div>

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form>
</div>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td></td>
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.name');" >Contractor Name</a></td>
		<td><a href="javascript: changeOrderBy('form1','atype.auditName');" >Audit</a></td>
		<s:iterator value="questions">
			<td><s:property value="columnHeaderOrQuestion"/></td>
		</s:iterator>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td colspan="2"><a href="ContractorView.action?id=<s:property value="get('id')"/>"
				><s:property value="get('name')" /></a></td>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
			<s:iterator value="questions">
				<td><s:property value="%{get('answer' + questionID)}"/></td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>			
</body>
</html>	