<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="download_results_tooltip">
    <s:text name="javascript.DownloadAllRows"><s:param>${report.allRows}</s:param></s:text>
</s:set>

<s:url action="ReportNewRequestedContractorCSV" var="download_requests_excel" />
<s:url action="ReportNewReqConImport" var="import_registration_request" />

<title>
    <s:text name="ReportNewRequestedContractor.title" />
</title>
<s:include value="reportHeader.jsp" />

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
    <h1>
        <s:text name="ReportNewRequestedContractor.title" />
    </h1>
    
    <s:include value="filters.jsp" />
    
    <div class="right">
        <a class="excel download-search-results" href="javascript:;" data-number-of-results="${report.allRows}" data-url="${download_requests_excel}"
            title="${download_results_tooltip}">
            <s:text name="global.Download" />
        </a>
    </div>
    <div style="padding: 5px;">
        <a href="RequestNewContractor.action" class="add" id="AddRegistrationRequest">
            <s:text name="ReportNewRequestedContractor.link.AddRegistrationRequest" />
        </a>
        <s:if test="accountManagerOrSalesRepresentative || debugging">
            <a
                href="javascript:;"
                title="<s:text name="javascript.OpensInNewWindow" />"
                class="add"
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
                        <a href="javascript: changeOrderBy('form1','cr.creationDate');">
                            <s:text name="global.CreationDate" />
                            <i class="icon-circle-arrow-right"></i>
                        </a>
                    </td>
                    <td>
                        <a href="javascript: changeOrderBy('form1','cr.deadline');">
                            <s:text name="ContractorRegistrationRequest.deadline" />
                            <i class="icon-circle-arrow-right"></i>
                        </a>
                    </td>
                    <td>
                        <s:text name="ReportNewRequestedContractor.label.ContactedBy" />
                    </td>
                    <td>
                        <a href="javascript: changeOrderBy('form1','cr.lastContactDate DESC');">
                            <s:text name="ReportNewRequestedContractor.label.On" />
                            <i class="icon-circle-arrow-right"></i>
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
                            ${stat.index + report.firstRowNumber}
                        </td>
                        <td>
                            <s:url action="RequestNewContractor" var="request_new_contractor">
                                <s:param name="newContractor">
                                    ${crr.get('id')}
                                </s:param>
                            </s:url>
                            <a href="${request_new_contractor}">
                                ${crr.get('name')}
                            </a>
                        </td>
                        <td title="${crr.get('RequestedUser')}">
                            ${crr.get('RequestedBy')}
                        </td>
                        <td class="report-date">
                            <s:date name="get('creationDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                        </td>
                        <td class="report-date">
                            <s:date name="get('deadline')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                        </td>
                        <td>
                            ${crr.get('ContactedBy')}
                        </td>
                        <td class="report-date">
                            <s:date name="get('lastContactDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                        </td>
                        <td>
                            ${crr.get('contactCount')}
                        </td>
                        <td>
                            ${crr.get('matchCount')}
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