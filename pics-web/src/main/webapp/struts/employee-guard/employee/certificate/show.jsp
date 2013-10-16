<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skills/certificate" method="editSkillSection" var="employee_skill_edit_url">
    <s:param name="id">${document.id}</s:param>
</s:url>
<s:url action="skills/certificate" method="delete" var="employee_certificate_delete_url">
    <s:param name="id">${document.id}</s:param>
</s:url>
<s:url action="skills/certificate" method="download" var="employee_certificate_download_url">
    <s:param name="id">${document.id}</s:param>
</s:url>

<%-- Page header --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Certificate</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Certificate
        </button>
    </s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${employee_certificate_delete_url}</s:param>
    <s:param name="modal_title">Delete Certificate</s:param>
    <s:param name="modal_message">Deleting will remove the certificate from all associated skills.</s:param>
</s:include>

<div class="row">
    <div class="col-md-9">
        <section class="employee-guard-section edit-container" data-url="${employee_skill_edit_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-picture icon-large"></i>
                        Certificate
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
                    <dt class="col-md-3">Proof</dt>
                    <dd class="col-md-9"><a href="${employee_certificate_download_url}">${document.fileName}</a></dd>
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
    <div class="col-md-3">
        <%-- Linked skills --%>
    </div>
</div>