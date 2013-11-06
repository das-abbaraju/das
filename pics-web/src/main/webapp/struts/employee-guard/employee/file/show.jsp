<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" method="editFileSection" var="employee_file_edit_url">
    <s:param name="id">${document.id}</s:param>
</s:url>
<s:url action="file" method="delete" var="employee_file_delete_url">
    <s:param name="id">${document.id}</s:param>
</s:url>
<s:url action="file" method="download" var="employee_file_download_url">
    <s:param name="id">${document.id}</s:param>
</s:url>

<%-- Page header --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">File: ${document.name}</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete File
        </button>
    </s:param>
    <s:param name="breadcrumb_name">${document.name}</s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${employee_file_delete_url}</s:param>
    <s:param name="modal_title">Delete File</s:param>
    <s:param name="modal_message">Deleting will remove the file and set all associated skills to incomplete.</s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <section class="employee-guard-section edit-container" data-url="${employee_file_edit_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-picture icon-large"></i>
                        File
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">Name</dt>
                    <dd class="col-md-9">${document.name}</dd>
                    <dt class="col-md-3">File</dt>
                    <dd class="col-md-9"><a href="${employee_file_download_url}">${document.fileName}</a></dd>
                    <dt class="col-md-3">Expires</dt>
                    <dd class="col-md-9">
                        <s:if test="document.doesNotExpire">
                            Never
                        </s:if>
                        <s:else>
                            ${document.endDate}
                        </s:else>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
    <div class="col-md-4">
        <%-- Linked skills --%>
    </div>
</div>