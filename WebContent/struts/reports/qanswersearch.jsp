<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Question Answer Search</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<script type="text/javascript">
var myTimer = null;

function getQuestionList() {
		var data = {
			questionName: $('[name=questionSelect]').val()
		};
		startThinking({div: 'thinking',message:'Finding matching questions...'});
		$('#selected_question').load('QuestionSelectAjax.action', data, function() {
			stopThinking({div: 'thinking'});
		});
}

function autoGetQuestion(ele) {
	if (ele.value.length > 3) {
		if (myTimer != null)
			clearTimeout(myTimer);
		myTimer = setTimeout("getQuestionList()",500);
	}
}

function setId(Id) {
	$('removeQuestionId').value = Id;
	return true;
}
</script>
</head>
<body>
<h1>Question Answer Search</h1>

<div id="search">
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;" onsubmit="runSearch('form1')">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
	<input type="hidden" value="" id="removeQuestionId" name="removeQuestionId">

	<s:iterator value="questions" status="stat">
		<div class="filterOption">
			<div>
				<s:hidden name="questions[%{#stat.index}].id" value="%{id}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].criteria" value="%{criteria}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].criteriaAnswer" value="%{criteriaAnswer}"></s:hidden>
				<s:property value="shortQuestion"/> <s:property value="criteria"/> <s:property value="criteriaAnswer"/>
				<button type="submit" class="picsbutton negative" name="button" value="Remove" onclick="javascript : return setId(<s:property value="id"/>);">Remove</button>
			</div>
		</div>
	</s:iterator>
	<br clear="all"/>
	<div class="filterOption">
		Select a Question
		<s:textfield id="questionSelect" cssClass="forms" name="questionSelect" size="35" onkeyup="autoGetQuestion(this)" /> 
		<input type="button" value="Search" onclick="getQuestionList()"/> <span id="thinking"></span><br/>
		<div id="selected_question">&nbsp;</div>
	</div>

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form>
</div>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

<s:if test="data.size > 0">
	<pics:permission perm="ContractorDetails">
		<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('QuestionAnswerSearch');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
	</pics:permission>
</s:if>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td></td>
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.name');" >Contractor Name</a></td>
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
			<s:iterator value="questions">
				<td><s:property value="%{get('answer' + id)}"/></td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>			
</body>
</html>	