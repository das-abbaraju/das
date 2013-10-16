<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" var="operator_skill_list_url" />
<s:url action="skill" method="create" var="operator_skill_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skills</s:param>
    <s:param name="actions">
        <a href="${operator_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Skill</a>
    </s:param>
</s:include>

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp" />

<tw:form formName="operator_skill_search" action="${operator_skill_list_url}">
    <fieldset>
        <tw:input inputName="search" type="text" class="search-query col-md-4" placeholder="Search by name" />
    </fieldset>
</tw:form>

<table class="table table-striped table-bordered table-condensed">
    <tr>
        <th class="col-md-5">Skill</th>
        <th class="col-md-7">Roles</th>
    </tr>

    <s:iterator value="skills" var="skill">
        <s:url action="skill" var="operator_skill_show_url">
            <s:param name="id">${skill.id}</s:param>
        </s:url>

        <tr>
            <td><a href="${operator_skill_show_url}">${skill.name}</a></td>
            <td>
                <s:set name="operator_roles" value="#skill.groups"/>
                <s:include value="/struts/employee-guard/operator/role/_list.jsp" />
            </td>
        </tr>
    </s:iterator>
</table>