<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
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
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('QuestionAnswerSearchByAudit');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
	</pics:permission>
</s:if>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td></td>
		<td colspan="2"><a href="javascript: changeOrderBy('form1','a.nameIndex DESC');" ><s:text name="global.ContractorName" /></a></td>
		<td>Status</td>
		<td><a href="javascript: changeOrderBy('form1','cao.statusChangedDate DESC');" >Status Changed Date</a></td>
		<s:iterator value="auditQuestions">
			<td><s:property value="columnHeaderOrQuestion"/></td>
		</s:iterator>
				<s:if test="showContact">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
			<td><s:text name="global.OfficeAddress" /></td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
			<td><s:text name="global.ZipPostalCode" /></td>
			<td><s:text name="ContractorAccount.webUrl" /></td>
		</s:if>
		<s:if test="showTrade">
			<td><s:text name="Trade" /></td>
			<td>Self Performed</td>
			<td>Sub Contracted</td>			
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td colspan="2"><a href="ContractorView.action?id=<s:property value="get('id')"/>"
				><s:property value="get('name')" /></a></td>
			<td><s:property value="get('auditStatus')"/></td>
			<td><s:date name="get('statusChangedDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
			<s:iterator value="auditQuestions">
				<td><s:property value="%{get('answer' + id)}"/></td>
			</s:iterator>
			<s:if test="showContact">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('countrySubdivision')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
				<td><s:property value="get('tradesSelf')"/></td>
				<td><s:property value="get('tradesSub')"/></td>			
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>			
</body>
</html>	