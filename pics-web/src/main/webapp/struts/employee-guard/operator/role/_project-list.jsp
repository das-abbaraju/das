<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#role_projects.isEmpty()">
    <ul class="employee-guard-list edit-display-values">
        <s:iterator value="#role_projects" var="role_project">
            <s:if test="#role_project.project.deletedBy == 0 && #role_project.project.deletedDate == null">
                <s:url action="project" var="role_project_show_url">
                    <s:param name="id">${role_project.project.id}</s:param>
                </s:url>

                <li>
                    <a href="${role_project_show_url}"><span class="label label-pics">${role_project.project.name}</span></a>
                </li>
            </s:if>
        </s:iterator>
    </ul>
</s:if>