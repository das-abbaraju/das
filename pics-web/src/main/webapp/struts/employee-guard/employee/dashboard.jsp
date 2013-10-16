<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="profile" var="employee_profile_show_url">
    <s:param name="id">
        <s:if test="profile != null">
            ${profile.id}
        </s:if>
        <%-- TODO: Remove this in the future --%>
        <s:else>
            1
        </s:else>
    </s:param>
</s:url>
<%--<s:url action="profile/settings" var="employee_profile_settings_url" />--%>
<s:url action="skills" var="employee_skills_list_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">${permissions.name}</s:param>
    <s:param name="subtitle">
        <s:if test="profile.slug != null">
            ${profile.slug}
        </s:if>
        <s:else>
            ${permissions.email}
        </s:else>
    </s:param>
    <s:param name="breadcrumbs">false</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
<%--         <li>
            <a href="${employee_profile_badge_url}"><i class="icon-qrcode"></i> Live ID</a>
        </li> --%>
        <li>
            <a href="${employee_skills_list_url}"><i class="icon-certificate"></i> Skills</a>
        </li>
        <li>
            <a href="${employee_profile_show_url}"><i class="icon-user"></i> Profile</a>
        </li>
<%--         <li>
            <a href="${employee_profile_settings_url}"><i class="icon-cog"></i> Settings</a>
        </li> --%>
    </ul>
<%--     <div class="col-md-9">
        <section class="employee-guard-section">
            <h1>Updates</h1>

            <div class="content"></div>
        </section>

        <section class="employee-guard-section">
            <h1>Site Status</h1>

            <div class="content"></div>
        </section>

        <section class="employee-guard-section">
            <h1>Skill Alerts</h1>

            <div class="content"></div>
        </section>
    </div> --%>
</div>