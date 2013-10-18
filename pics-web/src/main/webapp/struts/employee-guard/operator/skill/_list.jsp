<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#operator_skills.isEmpty()">
    <ul class="employee-guard-list skills">
        <s:iterator value="#operator_skills" var="operator_skill">
            <s:url action="skill" var="operator_skill_show_url">
                <s:param name="id">${operator_skill.skill.id}</s:param>
            </s:url>

            <li>
                <a href="${operator_skill_show_url}"><span class="label label-pics">${operator_skill.skill.name}</span></a>
            </li>
        </s:iterator>
    </ul>
</s:if>