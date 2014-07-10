<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="edit_method" value="%{skillDocumentForm.skillInfo.skillType.training ? 'training' : 'file'}" />
<s:url action="skill" method="%{#edit_method}" var="edit_employee_skill_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="EMPLOYEE.SKILL.SHOW.HEADER"/> ${skillDocumentForm.skillInfo.name}</s:param>
    <s:param name="breadcrumb_name">${skillDocumentForm.skillInfo.name}</s:param>
</s:include>

<div class="row">
    <div class="col-md-8 skill-show">
        <div class="edit-container" data-url="${edit_employee_skill_url}">
            <%-- if skill is incomplete --%>
            <s:if test="skillDocumentForm.skillInfo.skillStatus.expired">
                <s:include value="/struts/employee-guard/_action-message.jsp">
                    <s:param name="type">danger</s:param>
                    <s:param name="message">
                        <s:text name="EMPLOYEE.SKILL.SHOW.MSG.INCOMPLETE"/>
                    </s:param>
                </s:include>
            </s:if>

            <%-- if skill is expiring --%>
            <s:if test="skillDocumentForm.skillInfo.skillStatus.expiring">
                <s:include value="/struts/employee-guard/_action-message.jsp">
                    <s:param name="type">warning</s:param>
                    <s:param name="message">
                        <s:text name="EMPLOYEE.SKILL.SHOW.MSG.EXPIRING_SOON"/>
                    </s:param>
                </s:include>
            </s:if>

            <p class="description">${skillDocumentForm.skillInfo.description}</p>

            <dl class="employee-guard-information edit-display-values">
                <dt class="col-md-3"><s:text name="EMPLOYEE.SKILL.SHOW.FILE"/></dt>
                <dd class="col-md-9">${skillDocumentForm.proof}</dd>

                <s:if test="skillDocumentForm.proof != 'None'">
                    <dt class="col-md-3"><s:text name="EMPLOYEE.SKILL.SHOW.EXPIRES"/></dt>
                    <dd class="col-md-9">
                        <s:if test="skillDocumentForm.doesNotExpire">
                            <s:text name="EMPLOYEE.SKILL.SHOW.NEVER_EXPIRES"/>
                        </s:if>
                        <s:else>
                            ${skillDocumentForm.skillInfo.endDate}
                        </s:else>
                    </dd>
                </s:if>
            </dl>

            <div class="col-md-9 col-md-offset-3">
                <button class="btn btn-default update edit-toggle"><s:text name="EMPLOYEE.SKILL.SHOW.UPDATE.BUTTON"/></button>
            </div>
        </div>
    </div>
</div>