<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" method="delete" var="contractor_employee_delete_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>
<s:url action="employee" method="editPersonalSection" var="contractor_employee_personal_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee" method="editEmploymentSection" var="contractor_employee_employment_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee" method="editAssignmentSection" var="contractor_employee_assignments_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" var="contractor_role_url" />


<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Employee: ${employee.firstName} ${employee.lastName}</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Employee</button>
    </s:param>
    <s:param name="breadcrumb_id">
        ${id}
    </s:param>
    <s:param name="breadcrumb_name">
        ${employee.firstName} ${employee.lastName}
    </s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${contractor_employee_delete_url}</s:param>
    <s:param name="modal_title">Delete Employee</s:param>
    <s:param name="modal_message">Deleting will remove all company data and requirements for this person.</s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <s:include value="/struts/employee-guard/employee/photo/_employee-photo-form.jsp">
            <s:url action="employee" method="edit" var="photo_edit_url">
                <s:param name="id">${employee.id}</s:param>
            </s:url>
            <s:url action="contractor/{contractorId}/employee-photo/{id}" namespace="/employee-guard/employee" var="image_url">
                <s:param name="contractorId">
                    ${employee.accountId}
                </s:param>
                <s:param name="id">
                    ${id}
                </s:param>
            </s:url>
            <s:set var="alt_text">
                ${employee.firstName} ${employee.lastName}
            </s:set>
        </s:include>

        <section class="employee-guard-section-full">
            <h1><i class="icon-certificate icon-large"></i>Required Skills</h1>

            <div class="content">
                <div class="list-group skill-list">
                    <s:iterator var="skill_info" value="skillInfoList">
                        <s:url action="skill" var="employee_skill_url">
                            <s:param name="id">${skill_info.id}</s:param>
                        </s:url>

                        <s:if test="#skill_info.skillStatus.expired">
                            <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                            <s:set var="skill_status_class">danger</s:set>
                        </s:if>
                        <s:elseif test="#skill_info.skillStatus.expiring">
                            <s:set var="skill_icon">icon-warning-sign</s:set>
                            <s:set var="skill_status_class">warning</s:set>
                        </s:elseif>
                        <s:elseif test="#skill_info.skillStatus.pending">
                            <s:set var="skill_icon">icon-ok-circle</s:set>
                            <s:set var="skill_status_class">success</s:set>
                        </s:elseif>
                        <s:elseif test="#skill_info.skillStatus.completed">
                            <s:set var="skill_icon">icon-ok-sign</s:set>
                            <s:set var="skill_status_class">success</s:set>
                        </s:elseif>

                        <s:if test="permissions.accountId == #skill_info.accountId">
                            <s:set var="skill_url">
                                <s:url action="skill">
                                    <s:param name="id">
                                        ${skill_info.id}
                                    </s:param>
                                </s:url>
                            </s:set>
                            <a href="${skill_url}" class="list-group-item ${skill_status_class}">
                                <i class="${skill_icon}"></i>${skill_info.name}
                            </a>
                        </s:if>
                        <s:else>
                            <div class="list-group-item ${skill_status_class} operator-skill">
                                <div class="row">
                                    <div class="col-xs-10 col-sm-11 col-md-10">
                                        <i class="${skill_icon}"></i>${skill_info.name}
                                    </div>
                                    <div class="col-xs-2 col-sm-1 col-md-2 text-center">
                                        <i class="icon-map-marker" data-toggle="tooltip" data-placement="top" title="" data-original-title="Skill required due to assignment." data-container="body"></i>
                                    </div>
                                </div>
                            </div>
                        </s:else>

                    </s:iterator>
                </div>
            </div>
        </section>
    </div>

    <div class="col-md-9">
        <section class="employee-guard-section edit-container" data-url="${contractor_employee_personal_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-user icon-large"></i> Personal
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">First Name</dt>
                    <dd class="col-md-9">${employee.firstName}</dd>
                    <dt class="col-md-3">Last Name</dt>
                    <dd class="col-md-9">${employee.lastName}</dd>
                    <dt class="col-md-3">Email</dt>
                    <dd class="col-md-9">${employee.email}</dd>
                    <dt class="col-md-3">Phone</dt>
                    <dd class="col-md-9">${employee.phone}</dd>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section edit-container" data-url="${contractor_employee_employment_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-file-text-alt icon-large"></i>
                        Employment
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">Employee ID</dt>
                    <dd class="col-md-9">${employee.slug}</dd>
                    <dt class="col-md-3">Title</dt>
                    <dd class="col-md-9">${employee.positionName}</dd>
                    <%-- <dt class="col-md-3">Classification</dt>
                    <dd>${employee.positionType.displayName}</dd> --%>
                    <dt class="col-md-3">
                        <a href="${contractor_role_url}">Employee Groups</a>
                    </dt>
                    <dd class="col-md-9">
                        <s:set var="contractor_groups" value="employee.groups"/>
                        <s:include value="/struts/employee-guard/contractor/group/_list.jsp"/>
                    </dd>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section" data-url="${contractor_employee_assignments_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-map-marker icon-large"></i>
                        Current Assignments
                    </div>
                </div>
            </h1>

            <div class="content">
                <s:if test="!employeeAssignments.isEmpty()">
                    <dl class="employee-guard-information">
                        <s:iterator var="employee_assignment" value="employeeAssignments">
                            <dt class="col-md-3">${employee_assignment.siteName}</dt>
                            <dd class="col-md-9">
                                <s:if test="!#employee_assignment.projects.isEmpty()">
                                    <ul class="employee-guard-list roles">
                                        <s:iterator var="project" value="#employee_assignment.projects">
                                            <s:url action="project/{projectId}/assignments/{id}" var="contractor_project_assignment_url">
                                                <s:param name="projectId">${project.id}</s:param>
                                                <s:param name="id">${employee_assignment.siteId}</s:param>
                                            </s:url>

                                            <li>
                                                <a href="${contractor_project_assignment_url}">
                                                    <span class="label label-pics">${project.name}</span>
                                                </a>
                                            </li>
                                        </s:iterator>
                                    </ul>
                                </s:if>
                            </dd>
                        </s:iterator>
                    </dl>
                </s:if>
                <s:else>
                    <div class="col-md-9 col-md-offset-3 no-value">
                        No current assignments
                    </div>
                </s:else>
            </div>
        </section>
    </div>
</div>