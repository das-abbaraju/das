<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="profile" method="editPersonalSection" var="employee_personal_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" var="employee_skill_list_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Profile</s:param>
    <s:param name="breadcrumb_name">${profile.firstName} ${profile.lastName}</s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <s:include value="/struts/employee-guard/employee/photo/_employee-photo-form.jsp">
            <s:url action="profile" method="edit" var="photo_edit_url">
                <s:param name="id">${profile.id}</s:param>
            </s:url>
            <s:url action="profile" method="photo" var="image_url">
                <s:param name="id">
                    ${profile.id}
                </s:param>
            </s:url>
            <s:set var="alt_text">
                ${profile.firstName} ${profile.lastName}
            </s:set>
        </s:include>

        <section class="employee-guard-section-full">
            <h1><i class="icon-certificate icon-large"></i>Required Skills</h1>

            <div class="content">
                <div class="list-group skill-list">
                    <s:iterator var="skill_info" value="employeeProfileForm.skillInfoList" >
                        <s:set var="skill_status">${skill_info.skillStatus.displayValue}</s:set>
                        <s:url action="skill" var="employee_skill_url">
                            <s:param name="id">${skill_info.id}</s:param>
                        </s:url>

                        <s:set var="skill_icon">icon-ok-sign</s:set>
                        <s:if test="#skill_info.skillStatus.expired" >
                            <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                        </s:if>
                        <s:elseif test="#skill_info.skillStatus.expiring" >
                            <s:set var="skill_icon">icon-warning-sign</s:set>
                        </s:elseif>
                        <s:elseif test="#skill_info.skillStatus.pending" >
                            <s:set var="skill_icon">icon-ok-circle</s:set>
                        </s:elseif>
                        <s:elseif test="#skill_info.skillStatus.complete">
                            <s:set var="skill_icon">icon-ok-sign</s:set>
                        </s:elseif>

                        <a href="${employee_skill_url}" class="list-group-item ${skill_status}">
                            <i class="${skill_icon}"></i>${skill_info.name}
                        </a>
                    </s:iterator>
                </div>
            </div>
        </section>
    </div>

    <div class="col-md-9">
        <section class="employee-guard-section edit-container" data-url="${employee_personal_edit_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-user icon-large"></i>
                        Personal
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3">First Name</dt>
                    <dd class="col-md-9">${employeeProfileForm.personalInformation.firstName}</dd>
                    <dt class="col-md-3">Last Name</dt>
                    <dd class="col-md-9">${employeeProfileForm.personalInformation.lastName}</dd>
                    <dt class="col-md-3">Email</dt>
                    <dd class="col-md-9">${employeeProfileForm.personalInformation.email}</dd>
                    <dt class="col-md-3">Phone</dt>
                    <dd class="col-md-9">${employeeProfileForm.personalInformation.phone}</dd>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section" id="employment">
            <h1><i class="icon-file-text-alt"></i> Current Employment</h1>
            <div class="content">
                <s:iterator value="employeeProfileForm.companyGroupInfoList" var="company_info">
                    <dl class="employee-guard-information">
                        <dt class="col-md-3">
                            ${company_info.accountModel.name}
                        </dt>
                        <dd class="col-md-9">
                            <ul class="employee-guard-list companies">
                                <s:iterator value="groupInfoList" var="group">
                                    <li>
                                        <a href="#"><span class="label label-default">${group.name}</span></a>
                                    </li>
                                </s:iterator>
                            </ul>
                        </dd>
                    </dl>
                </s:iterator>
            </div>

            <%--<div class="content">
                <dl class="employee-guard-information">
                    <dt class="col-md-3">
                        PICS
                    </dt>
                    <dd class="col-md-9">
                        <ul class="employee-guard-list companies">
                            <li>
                                <a href="#"><span class="label">Technology</span></a>
                            </li>
                            <li>
                                <a href="#"><span class="label">UX</span></a>
                            </li>
                        </ul>
                    </dd>
                </dl>
            </div>--%>
        </section>

        <section class="employee-guard-section" id="assignments">
            <h1><i class="icon-map-marker"></i> Current Assignments</h1>

            <div class="content">
                <dl class="employee-guard-information">
                    <dt class="col-md-3">
                        PICS
                    </dt>
                    <dd class="col-md-9">
                        <ul class="employee-guard-list companies">
                            <li>
                                <label class="label label-default">Technology</label>
                            </li>
                            <li>
                                <label class="label label-default">PICS</label>
                            </li>
                        </ul>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
</div>