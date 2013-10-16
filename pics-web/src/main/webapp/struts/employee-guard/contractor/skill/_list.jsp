<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#contractor_skills.isEmpty()">
    <ul class="employee-guard-list skills">
        <s:iterator value="#contractor_skills" var="contractor_skill">
            <s:url action="skill" var="contractor_skill_show_url">
                <s:param name="id">${contractor_skill.skill.id}</s:param>
            </s:url>

            <li>
                <a href="${contractor_skill_show_url}"><span class="label label-pics">${contractor_skill.skill.name}</span></a>
            </li>
        </s:iterator>
    </ul>
</s:if>