<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
var downloadAll = '<s:text name="javascript.ConfirmDownloadAllRows"><s:param><s:property value="report.allRows" /></s:param></s:text>'
function download() {
	newurl = "ReportNewRequestedContractorCSV.action?" + $('#form1').serialize();
	popupWin = window.open(newurl, 'ReportNewRequestedContractor', '');
}
function showExcelUpload() {
	url = 'ReportNewReqConImport.action';
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}
</script>
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>
<s:include value="filters.jsp" />
<div class="right"><a class="excel" 
	<s:if test="report.allRows > 500">onclick="return confirm(downloadAll);"</s:if> 
		href="javascript: download();"
		title="<s:text name="javascript.DownloadAllRows"><s:param><s:property value="report.allRows" /></s:param></s:text>"><s:text name="global.Download" /></a>
</div>
<form id="test" action="ReportNewRequestedContractorImport.action" method="post">
	<div style="padding: 5px;">
	<a href="RequestNewContractor.action" class="add"><s:text name="%{scope}.link.AddRegistrationRequest" /></a>
	<s:if test="amSales">
		<a class="add" onclick="showExcelUpload(); return false;" href="#"
			title="<s:text name="javascript.OpensInNewWindow" />"><s:text name="%{scope}.link.ImportRegistrationRequests" /></a>
	</s:if>
	</div>
</form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><s:text name="global.Account.name" /></td>
		<td><s:text name="ContractorRegistrationRequest.requestedBy" /></td>
		<td>
			<a href="javascript: changeOrderBy('form1','cr.deadline');">
				<s:text name="ContractorRegistrationRequest.deadline" />
			</a>
		</td>
		<td><s:text name="%{scope}.label.FollowUp" /></td>
		<td><s:text name="%{scope}.label.ContactedBy" /></td>
		<td>
			<a href="javascript: changeOrderBy('form1','cr.lastContactDate DESC');"><s:text name="%{scope}.label.On" /></a>
		</td>
		<td><s:text name="%{scope}.label.Attempts" /></td>
		<td title="<s:text name="%{scope}.label.PotentialMatches" />"><s:text name="%{scope}.label.Matches" /></td>
		<td>
			<a href="javascript: changeOrderBy('form1','cr.creationDate');">
				<s:text name="global.CreationDate" />
			</a>
		</td>
		<td><s:text name="%{scope}.label.InPics" /></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="get('id')"/>">
				<s:property value="get('name')" /></a>
			</td>
			<td title="<s:property value="get('RequestedUser')"/>">
				<s:property value="get('RequestedBy')"/>
			</td>
			<td><s:date name="get('deadline')" format="MM/dd/yyyy"/></td>
			<td><s:property value="get('handledBy')"/></td>
			<td><s:property value="get('ContactedBy')" /></td>
			<td><s:date name="get('lastContactDate')" format="MM/dd/yyyy"/></td>
			<td><s:property value="get('contactCount')" /></td>
			<td><s:property value="get('matchCount')" /></td>
			<td><s:date name="get('creationDate')" /></td>
			<td><s:if test="get('conID') != null">
					<a href="ContractorView.action?id=<s:property value="get('conID')"/>">
					<s:property value="get('contractorName')" /></a>			
				</s:if>
			</td>
		</tr>
	</s:iterator>

</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
