<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageRecommendedCSRAssignments" method="save" var="save_approved" />

<s:form action="%{#save_approved}" name="save_approved_form" id="save_approved_form">
    <input type="hidden" value="" name="acceptRecommendations" id="accepted" />
    <input type="hidden" value="" name="rejectRecommendations" id="rejected" />
    <button class="btn btn-primary" id="save_assignments" type="submit">Save</button>

	<table class="table table-striped" id="csr_assignments">
	    <thead>
	        <tr>
	            <th class="name">Contractor ID</th>
	            <th class="value">Contractor Name</th>
	            <th class="actions">Current CSR</th>
	            <th class="actions">Recommended CSR</th>
	            <th class="actions">Action</th>
	        </tr>
	    </thead>
	    <tbody>
	        <s:iterator value="queryResults" var="row">
	            <tr>
	                <td class="account_id">${row.get("AccountID")}</td>
	                <td>${row.get("AccountName")}</td>
	                <td>${row.get("ContractorCustomerServiceUserName")}</td>
	                <td>${row.get("ContractorRecommendedCSRName")}</td>
	                <td>
	                    <div id="text-toggle-button" class="accept button">
	                        <input type="checkbox" class="status" checked="checked" />
	                    </div>
	                </td>
	            </tr>
	        </s:iterator>
	    </tbody>
	</table>
</s:form>