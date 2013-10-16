<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="skill" method="delete" var="contractor_skill_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skill" method="deleteConfirmation" var="contractor_skill_delete_confirmation_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Skill</s:param>
    <s:param name="actions">
        <a href="${contractor_skill_delete_url}" class="btn btn-danger delete" data-url="${contractor_skill_delete_confirmation_url}">Delete</a>
    </s:param>
</s:include>



<s:include value="/struts/employee-guard/contractor/skill/_edit-form.jsp" />