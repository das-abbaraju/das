<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" method="delete" var="operator_role_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="role" method="editNameSkillsSection
" var="operator_role_name_skills_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Job Role: ${role.name}</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Job Role
        </button>
    </s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${operator_role_delete_url}</s:param>
    <s:param name="modal_title">Delete Job Role</s:param>
    <s:param name="modal_message">Deleting will remove the job role and its required skills.</s:param>
</s:include>

<div class="row">
    <div class="col-md-9">
        <section class="employee-guard-section edit-container" data-url="${operator_role_name_skills_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-group icon-large"></i> Role
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">Name</dt>
                    <dd class="col-md-9">${role.name}</dd>
                    <dt class="col-md-3">Required Skills</dt>
                    <dd class="col-md-9">
                        <s:set var="operator_skills" value="role.skills"/>
                        <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
</div>