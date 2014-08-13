<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" method="delete" var="operator_skill_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="editSkillSection" var="operator_skill_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:if test="permissions.accountId == skill.accountId">
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="CORPORATE.SKILLS.SHOW.SKILL.HEADER"/> ${skill.name}</s:param>
        <s:param name="actions">
            <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger"><s:text name="CORPORATE.SKILLS.SHOW.DELETE.BUTTON"/>
            </button>
        </s:param>
        <s:param name="breadcrumb_name">${skill.name}</s:param>
    </s:include>
</s:if>
<s:else>
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.HEADER"/> ${skill.name}</s:param>
        <s:param name="breadcrumb_name">${skill.name}</s:param>
    </s:include>
</s:else>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${operator_skill_delete_url}</s:param>
    <s:param name="modal_title"><s:text name="CORPORATE.SKILLS.SHOW.DELETE.MODAL.TITLE"/></s:param>
    <s:param name="modal_message"><s:text name="CORPORATE.SKILLS.SHOW.DELETE.MODAL.MSG"/></s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <section class="employee-guard-section edit-container" data-url="${operator_skill_edit_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-certificate icon-large"></i> <s:text name="OPERATOR.SKILLS.SHOW.SKILL"/>
                    </div>
                    <s:if test="permissions.accountId == skill.accountId">
                        <div class="col-md-3 col-xs-3 edit">
                            <i class="icon-edit icon-large edit-toggle"></i>
                        </div>
                    </s:if>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.NAME"/></dt>
                    <dd class="col-md-9">${skill.name}</dd>
                    <dt class="col-md-3"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.DESCRIPTION"/></dt>
                    <dd class="col-md-9">${skill.description}</dd>
                    <dt class="col-md-3"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.SKILL_TYPE"/></dt>
                    <dd class="col-md-9">
                        <%-- Skill Type --%>
                        <s:include value="/struts/employee-guard/_skilltype.jsp">
                            <s:param name="skillType">${skill.skillType}</s:param>
                        </s:include>
                    </dd>

                    <s:if test="skill.skillType.training">
                        <dt class="col-md-3"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.EXPIRES_AFTER"/></dt>
                        <dd class="col-md-9">
                            <s:if test="skill.intervalType.applicableExpiration">
                                ${skill.intervalPeriod}
                                <%-- Interval Type --%>
                                <s:include value="/struts/employee-guard/_interval_type.jsp">
                                    <s:param name="intervalType">${skill.intervalPeriod}</s:param>
                                </s:include>
                                ${skill.intervalType.displayValue}<s:if test="skill.intervalPeriod != 1">s</s:if>
                            </s:if>
                            <s:else>
                                <s:text name="OPERATOR.SKILLS.SHOW.NEVER_EXPIRES"/>
                            </s:else>
                        </dd>
                    </s:if>

                    <dt class="col-md-3"><s:text name="OPERATOR.SKILLS.SHOW.SKILL.JOB_ROLES"/></dt>
                    <dd class="col-md-9">
                        <s:if test="skill.ruleType.required">
                            <s:text name="OPERATOR.SKILLS.SHOW.REQUIRED_FOR_ALL_EMPLOYEES"/>
                        </s:if>
                        <s:else>
                            <s:set var="operator_roles" value="skill.roles"/>

                            <s:include value="/struts/employee-guard/operator/role/_list.jsp"/>
                        </s:else>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
    <%-- Not MVP Markup
        <div class="col-md-4">
            <section class="employee-guard-section edit-container" data-url="${operator_skill_edit_url}">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">
                            <i class="icon-user icon-large"></i> Skill Status
                        </div>
                        <div class="col-md-3 col-xs-3 edit">
                            <i class="icon-edit icon-large edit-toggle"></i>
                        </div>
                    </div>
                </h1>

                <div class="content">
                    <dl class="employee-guard-information edit-display-values">
                    </dl>
                </div>
            </section>
        </div> --%>
</div>