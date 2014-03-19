<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!#operator_skills.isEmpty()">
    <ul class="employee-guard-list skills">
        <s:iterator value="#operator_skills" var="operator_skill">
            <s:url action="skill" var="operator_skill_show_url">
                <s:param name="id">${operator_skill.skill.id}</s:param>
            </s:url>

            <li>
                <s:if test="permissions.operatorCorporate">
                    <a href="${operator_skill_show_url}"><span class="label label-pics">${operator_skill.skill.name}</span></a>
                </s:if>
                <s:else>
                    <span class="label label-default" data-toggle="tooltip" data-placement="right" title="" data-original-title="${operator_skill.skill.description}" data-container="body">${operator_skill.skill.name}</span>
                </s:else>
            </li>
        </s:iterator>
    </ul>
</s:if>