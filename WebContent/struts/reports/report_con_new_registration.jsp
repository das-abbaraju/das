<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:text name="ReportNewRequestedContractor.title" />
		</title>
		<s:include value="reportHeader.jsp" />
	</head>
	<body>
		<div id="${actionName}-page">
			<h1>
				<s:text name="ReportNewRequestedContractor.title" />
			</h1>
			<s:include value="filters.jsp" />
			<div class="right">
				<a class="excel" rel="<s:property value="report.allRows" />" href="javascript:;" 
					title="<s:text name="javascript.DownloadAllRows"><s:param><s:property value="report.allRows" /></s:param></s:text>">
					<s:text name="global.Download" />
				</a>
			</div>
			<div style="padding: 5px;">
				<a href="RequestNewContractor.action" class="add" id="AddRegistrationRequest">
					<s:text name="ReportNewRequestedContractor.link.AddRegistrationRequest" />
				</a>
				<s:if test="amSales || debugging">
					<a
						href="javascript:;"
						title="<s:text name="javascript.OpensInNewWindow" />"
						class="add excelUpload"
						data-url="ReportNewReqConImport.action"
						id="ImportRegistrationRequests">
						<s:text name="ReportNewRequestedContractor.link.ImportRegistrationRequests" />
					</a>
				</s:if>
			</div>
			<s:if test="data.size > 0">
				<div>
					<s:property value="report.pageLinksWithDynamicForm" escape="false" />
				</div>
				<table class="report">
					<thead>
						<tr>
							<td colspan="2">
								<s:text name="global.Account.name" />
							</td>
							<td>
								<s:text name="ContractorRegistrationRequest.requestedBy" />
							</td>
							<td>
								<a href="javascript: changeOrderBy('form1','cr.creationDate');">
									<s:text name="global.CreationDate" />
								</a>
							</td>
							<td>
								<a href="javascript: changeOrderBy('form1','cr.deadline');">
									<s:text name="ContractorRegistrationRequest.deadline" />
								</a>
							</td>
							<td>
								<s:text name="ReportNewRequestedContractor.label.ContactedBy" />
							</td>
							<td>
								<a href="javascript: changeOrderBy('form1','cr.lastContactDate DESC');">
									<s:text name="ReportNewRequestedContractor.label.On" />
								</a>
							</td>
							<td>
								<s:text name="ReportNewRequestedContractor.label.Attempts" />
							</td>
							<td title="<s:text name="ReportNewRequestedContractor.label.PotentialMatches" />">
								<s:text name="ReportNewRequestedContractor.label.Matches" />
							</td>
							<td>
								<s:text name="ReportNewRequestedContractor.label.InPics" />
							</td>
							<s:if test="filter.requestStatus.empty || filter.requestStatus.contains('Closed')">
								<td>
									<s:text name="ReportNewRequestedContractor.label.ClosedDate" />
								</td>
							</s:if>
							<td>
								<s:text name="RequestNewContractor.OperatorTags" />
							</td>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="data" status="stat" var="crr">
							<tr>
								<td class="right">
									<s:property value="#stat.index + report.firstRowNumber" />
								</td>
								<td>
									<a href="RequestNewContractor.action?newContractor=<s:property value="get('id')"/>">
										<s:property value="get('name')" />
									</a>
								</td>
								<td title="<s:property value="get('RequestedUser')"/>">
									<s:property value="get('RequestedBy')"/>
								</td>
								<td>
									<s:date name="get('creationDate')" />
								</td>
								<td>
									<s:date name="get('deadline')" />
								</td>
								<td>
									<s:property value="get('ContactedBy')" />
								</td>
								<td>
									<s:date name="get('lastContactDate')" />
								</td>
								<td>
									<s:property value="get('contactCount')" />
								</td>
								<td>
									<s:property value="get('matchCount')" />
								</td>
								<td>
									<s:if test="get('conID') != null">
										<a href="ContractorView.action?id=<s:property value="get('conID')"/>">
											<s:property value="get('contractorName')" />
										</a>			
									</s:if>
								</td>
								<s:if test="filter.requestStatus.empty || filter.requestStatus.contains('Closed')">
									<td>
										<s:date name="get('closedOnDate')" format="%{getText('date.short')}" />
									</td>
								</s:if>
								<td>
									<s:property value="get('operatorTags')" />
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<div>
				<s:property value="report.pageLinksWithDynamicForm" escape="false" />
			</div>
		</div>
	</body>
</html>
