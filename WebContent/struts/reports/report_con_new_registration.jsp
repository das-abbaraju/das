<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportNewRequestedContractor.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
$(function() {
	$('#content').delegate('a.excel', 'click', function(e) {
		e.preventDefault();
		var num = $(this).attr('rel');
		
		var confirmed = false;
		if (num > 500)
			confirmed = confirm(translate('JS.ConfirmDownloadAllRows', ['<s:property value="report.allRows" />']));
		else
			confirmed = true;
		
		if (confirmed) {
			newurl = "ReportNewRequestedContractorCSV.action?" + $('#form1').serialize();
			popupWin = window.open(newurl, 'ReportNewRequestedContractor', '');
		}
	});
	
	$('#test').delegate('.excelUpload', 'click', function(e) {
		e.preventDefault();
		
		var url = 'ReportNewReqConImport.action';
		var title = 'Upload';
		var pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	});
});
</script>
</head>
<body>
<h1><s:text name="ReportNewRequestedContractor.title" /></h1>
<s:include value="filters.jsp" />
<div class="right">
	<a class="excel" rel="<s:property value="report.allRows" />" href="#"
		title="<s:text name="javascript.DownloadAllRows"><s:param><s:property value="report.allRows" /></s:param></s:text>">
		<s:text name="global.Download" />
	</a>
</div>
<form id="test" action="ReportNewRequestedContractorImport.action" method="post">
	<div style="padding: 5px;">
	<a href="RequestNewContractor.action" class="add"><s:text name="ReportNewRequestedContractor.link.AddRegistrationRequest" /></a>
	<s:if test="amSales">
		<a class="add excelUpload" href="#" title="<s:text name="javascript.OpensInNewWindow" />">
			<s:text name="ReportNewRequestedContractor.link.ImportRegistrationRequests" />
		</a>
	</s:if>
	</div>
</form>
<s:if test="data.size > 0">
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
		<tr>
			<td colspan="2"><s:text name="global.Account.name" /></td>
			<td><s:text name="ContractorRegistrationRequest.requestedBy" /></td>
			<td><a href="javascript: changeOrderBy('form1','cr.creationDate');"><s:text name="global.CreationDate" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','cr.deadline');"><s:text name="ContractorRegistrationRequest.deadline" /></a></td>
			<td><s:text name="ReportNewRequestedContractor.label.FollowUp" /></td>
			<td><s:text name="ReportNewRequestedContractor.label.ContactedBy" /></td>
			<td>
				<a href="javascript: changeOrderBy('form1','cr.lastContactDate DESC');"><s:text name="ReportNewRequestedContractor.label.On" /></a>
			</td>
			<td><s:text name="ReportNewRequestedContractor.label.Attempts" /></td>
			<td title="<s:text name="ReportNewRequestedContractor.label.PotentialMatches" />"><s:text name="ReportNewRequestedContractor.label.Matches" /></td>
			<td><s:text name="ReportNewRequestedContractor.label.InPics" /></td>
			<s:if test="filter.open == 0">
				<td><s:text name="ReportNewRequestedContractor.label.ClosedDate" /></td>
			</s:if>
		</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="RequestNewContractor.action?newContractor=<s:property value="get('id')"/>">
					<s:property value="get('name')" /></a>
				</td>
				<td title="<s:property value="get('RequestedUser')"/>">
					<s:property value="get('RequestedBy')"/>
				</td>
				<td><s:date name="get('creationDate')" /></td>
				<td><s:date name="get('deadline')" format="MM/dd/yyyy"/></td>
				<td><s:property value="get('handledBy')"/></td>
				<td><s:property value="get('ContactedBy')" /></td>
				<td><s:date name="get('lastContactDate')" format="MM/dd/yyyy"/></td>
				<td><s:property value="get('contactCount')" /></td>
				<td><s:property value="get('matchCount')" /></td>
				<td><s:if test="get('conID') != null">
						<a href="ContractorView.action?id=<s:property value="get('conID')"/>">
						<s:property value="get('contractorName')" /></a>			
					</s:if>
				</td>
				<s:if test="filter.open == 0">
					<td><s:date name="getClosedDate(get('notes'))" /></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</s:if>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>

</body>
</html>
