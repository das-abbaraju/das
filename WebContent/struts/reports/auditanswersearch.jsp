<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Background Security Check</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function setId(Id) {
	$('#removeQuestionId').val(Id);
	return true;
}
</script>
</head>
<body>
<h1>Background Security Check</h1>
<s:include value="filters.jsp" />


<s:if test="data.size > 0">
	<pics:permission perm="ContractorDetails">
		<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('QuestionAnswerSearchByAudit');" 
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
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.nameIndex DESC');" >Contractor Name</a></td>
		<td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');" >Audit Status</a></td>
		<td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');" >Submitted</a></td>
		<s:iterator value="auditQuestions">
			<td><s:property value="columnHeaderOrQuestion"/></td>
		</s:iterator>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td colspan="2"><a href="ContractorView.action?id=<s:property value="get('id')"/>"
				><s:property value="get('name')" /></a></td>
			<td><s:property value="get('auditStatus')"/></td>
			<td><s:date name="get('completedDate')" format="M/d/yy"/></td>
			<s:iterator value="auditQuestions">
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