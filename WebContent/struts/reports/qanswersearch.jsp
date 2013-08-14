<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="QuestionAnswerSearch.title" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<script type="text/javascript">
var myTimer = null;

function getQuestionList() {
		var data = {
			questionName: $('[name="questionSelect"]').val()
		};
		startThinking({div: 'thinking', message: translate('JS.QuestionAnswerSearch.message.FindingMatchingQuestions')});
		$('#selected_question').load('QuestionSelectAjax.action', data, function() {
			stopThinking({div: 'thinking'});
		});
}

function autoGetQuestion(value) {
	if (value.length > 3) {
		if (myTimer != null)
			clearTimeout(myTimer);
		myTimer = setTimeout("getQuestionList()",500);
	}
}

function setId(Id) {
	$('#removeQuestionId').val(Id);
	return true;
}

$(function() {
	$('#searchButton').live('click', function() {
		getQuestionList();
	});
	
	$('.filterOption .negative').live('click', function() {
		var id = $(this).attr('id').split('_')[0];
		return setId(id);
	});
	
	$('#questionSelect').live('keyup', function() {
		autoGetQuestion($(this).val());
	});
	
	$('#orderByName').live('click', function(e) {
		e.preventDefault();
		changeOrderBy('form1','a.name');
	})
	
	$('#form1').one('submit', function() {
		runSearch($(this));
	});
});
</script>
</head>
<body>
<h1><s:text name="QuestionAnswerSearch.title" /></h1>

<div id="search">
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
	<input type="hidden" value="0" id="removeQuestionId" name="removeQuestionId">
	
	<input type="button" id="searchButton" class="picsbutton positive" value="<s:text name="button.Search" />"/><span id="thinking"></span><br/>

	<s:iterator value="questions" status="stat">
		<div class="filterOption">
			<div>
				<s:hidden name="questions[%{#stat.index}].id" value="%{id}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].criteria" value="%{criteria}"></s:hidden>
				<s:hidden name="questions[%{#stat.index}].criteriaAnswer" value="%{criteriaAnswer}"></s:hidden>
				<s:if test="columnHeader != null && columnHeader.size() > 0"><s:property value="columnHeader.toString()"/>: </s:if><s:property value="expandedNumber"/>: <s:property value="name"/> <s:property value="criteria"/> <s:property value="criteriaAnswer"/>
				<button type="submit" class="picsbutton negative" name="button" value="Remove" id="<s:property value="id"/>"><s:text name="button.Remove" /></button>
			</div>
		</div>
	</s:iterator>
	<br clear="all"/>
	<div class="filterOption">
		<s:text name="QuestionAnswerSearch.label.SelectAQuestion" />
		<s:textfield id="questionSelect" cssClass="forms" name="questionSelect" size="35" /> 
		<div id="selected_question">&nbsp;</div>
	</div>
	<div class="clear"></div>
	<div class="alphapaging"><s:property value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form>
</div>

<s:if test="data.size > 0">
	<pics:permission perm="ContractorDetails">
		<div class="right">
			<a class="excel" 
				<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
				href="javascript: download('QuestionAnswerSearch');" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
				<s:text name="global.Download" />
			</a>
		</div>
	</pics:permission>
</s:if>

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td></td>
		<td colspan="2"><a id="orderByName" href="#"><s:text name="global.CompanyName" /></a></td>
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