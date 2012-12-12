<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<head>
	<title>
		<s:text name="ReportNewRequestedContractor.title" />
	</title>
	<s:include value="reportHeader.jsp" />
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<h1>
			<s:text name="ReportNewRequestedContractor.title" />
		</h1>
		
		<s:include value="filters.jsp" />
		
		<div class="right">
			<s:url action="ReportRegistrationRequestsCSV" var="download_requests_excel" />
			<a class="excel"
				rel="${report.allRows}"
				href="javascript:;"
				title="<s:text name="javascript.DownloadAllRows"><s:param>${report.allRows}</s:param></s:text>"
				data-url="${download_requests_excel}">
				<s:text name="global.Download" />
			</a>
		</div>
		<div style="padding: 5px;">
			<s:url action="RequestNewContractorAccount" var="request_new_contractor" />
			<a href="${request_new_contractor}" class="add" id="AddRegistrationRequest">
				<s:text name="ReportNewRequestedContractor.link.AddRegistrationRequest" />
			</a>
			<s:if test="amSales || debugging">
				<s:url action="ReportNewReqConImport" var="import_registration_request" />
				<a
					href="javascript:;"
					title="<s:text name="javascript.OpensInNewWindow" />"
					class="add excelUpload"
					data-url="${import_registration_request}"
					id="ImportRegistrationRequests">
					<s:text name="ReportNewRequestedContractor.link.ImportRegistrationRequests" />
				</a>
			</s:if>
		</div>
		<s:if test="data.size > 0">
			<div>
				${report.pageLinksWithDynamicForm}
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
							<a href="javascript: changeOrderBy('form1','creationDate');">
								<s:text name="global.CreationDate" />
							</a>
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','deadline');">
								<s:text name="ContractorRegistrationRequest.deadline" />
							</a>
						</td>
						<td>
							<s:text name="global.Priority" />
						</td>
						<td>
							<s:text name="ReportNewRequestedContractor.label.ContactedBy" />
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','lastContactDate DESC');">
								<s:text name="ReportNewRequestedContractor.label.On" />
							</a>
						</td>
						<td>
							<s:text name="ReportNewRequestedContractor.label.Attempts" />
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
								${stat.index + report.firstRowNumber}
							</td>
							<td>
								<s:if test="#crr.get('systemType').toString().equals('ACC')">
									<s:url action="RequestNewContractorAccount" var="request_new_form">
										<s:param name="contractor">
											${crr.get('id')}
										</s:param>
										<s:param name="requestRelationship.operatorAccount">
											${crr.get('RequestedByID')}
										</s:param>
									</s:url>
								</s:if>
								<s:else>
									<s:url action="RequestNewContractor" var="request_new_form">
										<s:param name="newContractor">
											${crr.get('id')}
										</s:param>
									</s:url>
								</s:else>
								<a href="${request_new_form}">
									${crr.get('name')}
								</a>
							</td>
							<td title="${crr.get('RequestedUser')}">
								${crr.get('RequestedBy')}
							</td>
							<td>
								<s:date name="get('creationDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
							</td>
							<td>
								<s:date name="get('deadline')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
							</td>
							<td>
								<s:set var="inside_sales_priority" value="@com.picsauditing.jpa.entities.LowMedHigh@valueOf(get('priority').toString())" />
								<s:if test="#inside_sales_priority != null">
									<s:text name="%{#inside_sales_priority.i18nKey}" />
								</s:if>
							</td>
							<td>
								${crr.get('ContactedBy')}
							</td>
							<td>
								<s:date name="get('lastContactDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
							</td>
							<td>
								${crr.get('contactCount')}
							</td>
							<td>
								<s:if test="get('conID') != null">
									<s:url action="ContractorView" var="contractor_view">
										<s:param name="id">
											${crr.get('conID')}
										</s:param>
									</s:url>
									<a href="${contractor_view}">
										${crr.get('contractorName')}
									</a>			
								</s:if>
							</td>
							<s:if test="filter.requestStatus.empty || filter.requestStatus.contains('Closed')">
								<td>
									<s:date name="get('closedOnDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
								</td>
							</s:if>
							<td>
								${crr.get('operatorTags')}
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</s:if>
		<div>
			${report.pageLinksWithDynamicForm}
		</div>
	</div>
</body>