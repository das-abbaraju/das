<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageRecommendedCSRAssignment" method="save" var="save_approved" />
<s:url action="Report" var="report">
    <s:param name="report" value="107" />
</s:url>
<s:set name="totalRecords" value="queryResults.size()"/>

<div class="row">
    <div class="span6">
        <s:include value="/struts/layout/_page-header.jsp">
            <s:param name="title">Recommended CSR Assignment</s:param>
            <s:param name="subtitle"><i class="icon-external-link"></i><a href="${report}">Open Report</a></s:param>
        </s:include>
    </div>
    <div class="span5 offset1 apply-actions">
        <span><span id="selected_records">${totalRecords}</span> of ${totalRecords} Records Selected</span>
        <button class="btn btn-primary btn-large apply_selected_assignments">Apply Accepted</button>
    </div>
</div>

<form action="${save_approved}" name="save_approved_csr" id="save_approved_csr" method="post">
    <input type="hidden" value="" name="acceptRecommendations" id="acceptedCSR" />
    <input type="hidden" value="" name="rejectRecommendations" id="rejectedCSR" />

	<table class="table table-striped" id="csr_assignments">
	    <thead>
	        <tr>
                <s:iterator value="columns" var="column">
                    <th><s:text name="Report.%{#column.name}" /></th>
                </s:iterator>
	            <th class="actions"><input id="accept_all" type="checkbox" checked="checked" />Accept</th>
	        </tr>
	    </thead>
	    <tbody>
	        <s:iterator value="queryResults" var="row">
                <tr>
                    <s:iterator value="columns" var="column">
                        <td>${row.get(column)}</td>
                    </s:iterator>
	                <td>
                        <input type="checkbox" class="accept-recommended" checked="checked" value="${row.get("AccountID")}" />
	                </td>
	            </tr>
	        </s:iterator>
	    </tbody>
	</table>
    <div class="pull-right">
        <span><span id="selected_records">${totalRecords}</span> of ${totalRecords} Records Selected</span>
        <button class="btn btn-primary btn-large apply_selected_assignments">Apply Accepted</button>
    </div>
</form>