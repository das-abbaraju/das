<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" method="delete" var="contractor_skill_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="editSkillSection" var="contractor_skill_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skill: ${skill.name}</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Skill</button>
    </s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${contractor_skill_delete_url}</s:param>
    <s:param name="modal_title">Delete Skill</s:param>
    <s:param name="modal_message">Deleting will remove the skill from all employee groups and associated employees.</s:param>
</s:include>

<div class="row">
    <div class="col-md-9">
        <section class="employee-guard-section edit-container" data-url="${contractor_skill_edit_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-certificate icon-large"></i> Skill
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">Name</dt>
                    <dd class="col-md-9">${skill.name}</dd>
                    <dt class="col-md-3">Description</dt>
                    <dd class="col-md-9">${skill.description}</dd>
                    <dt class="col-md-3">Type</dt>
                    <dd class="col-md-9">${skill.skillType}</dd>

                    <s:if test="skill.skillType.training">
                        <dt class="col-md-3">Expires after&hellip;</dt>
                        <dd class="col-md-9">
                            <s:if test="skill.intervalType.applicableExpiration">
                                ${skill.intervalPeriod}
                                ${skill.intervalType.displayValue}<s:if test="skill.intervalPeriod != 1">s</s:if>
                            </s:if>
                            <s:else>
                                Never
                            </s:else>
                        </dd>
                    </s:if>

                    <dt class="col-md-3">Employee Groups</dt>
                    <dd class="col-md-9">
                        <s:if test="skill.ruleType.required">
                            Required for all employees
                        </s:if>
                        <s:else>
                            <s:set var="contractor_groups" value="skill.groups" />
                            <s:include value="/struts/employee-guard/contractor/group/_list.jsp" />
                        </s:else>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
<%--     <div class="col-md-3">
        <section class="employee-guard-section edit-container" id="skill_employees">
            <h1>
                <div class="row">
                    <div class="col-md-9">
                        <i class="icon-user icon-large"></i> Employees
                    </div>
                    <div class="col-md-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <s:set name="contractor_employees" value="skill.employees"/>
                <s:include value="/struts/employee-guard/contractor/employee/_list.jsp"/>
            </div>
        </section>
    </div> --%>
</div>