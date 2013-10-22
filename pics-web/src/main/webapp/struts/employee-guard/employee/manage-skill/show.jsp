<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="edit_method" value="%{skillDocumentForm.skillInfo.skillType.training ? 'training' : 'certification'}" />
<s:url action="skill" method="%{#edit_method}" var="edit_employee_skill_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skill: ${skillDocumentForm.skillInfo.name}</s:param>
</s:include>

<div class="row">
    <div class="col-md-8 skill-show">
        <div class="edit-container" data-url="${edit_employee_skill_url}">
            <%-- if skill is incomplete --%>
            <s:if test="skillDocumentForm.skillInfo.skillStatus.expired">
                <s:include value="/struts/employee-guard/_action-message.jsp">
                    <s:param name="type">danger</s:param>
                    <s:param name="message">
                        <strong>Incomplete!</strong> This required skill has not been completed.
                    </s:param>
                </s:include>
            </s:if>

            <%-- if skill is expiring --%>
            <s:if test="skillDocumentForm.skillInfo.skillStatus.expiring">
                <s:include value="/struts/employee-guard/_action-message.jsp">
                    <s:param name="type">warning</s:param>
                    <s:param name="message">
                        <strong>Notice!</strong> This required skill is expiring soon.
                    </s:param>
                </s:include>
            </s:if>

            <p class="description">${skillDocumentForm.skillInfo.description}</p>

            <dl class="employee-guard-information edit-display-values">
                <dt class="col-md-3">Proof</dt>
                <dd class="col-md-9">${skillDocumentForm.proof}</dd>
                <dt class="col-md-3">Expires</dt>
                <dd class="col-md-9">
                    <s:if test="skillDocumentForm.skillInfo.doesNotExpire">
                        Never
                    </s:if>
                    <s:else>
                        ${skillDocumentForm.skillInfo.endDate}
                    </s:else>
                </dd>
            </dl>

            <div class="col-md-9 col-md-offset-3">
                <button class="btn btn-default update edit-toggle">Update</button>
            </div>
        </div>
    </div>
        <%-- if training --%>
<%--         <s:if test="skillDocumentForm.skillInfo.skillType.training">
            <s:include value="/struts/employee-guard/employee/manage-skill/_training-form.jsp" />
        </s:if>
        <s:else>
            <s:include value="/struts/employee-guard/employee/manage-skill/_certification-form.jsp" />
        </s:else> --%>

    <%--<div class="col-md-4 well well-required-for">Required For:
        <ul>
            <li>PICS</li>
        </ul>
    </div>--%>
</div>