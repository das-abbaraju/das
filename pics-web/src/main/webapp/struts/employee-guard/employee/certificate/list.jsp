<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skills/certificate" var="employee_skill_list_url"/>
<s:url action="skills/certificate" method="create" var="employee_skill_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Certificates</s:param>
    <s:param name="actions">
        <a href="${employee_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Certificate</a>
    </s:param>
</s:include>


<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp"/>

<tw:form formName="employee_skill_search" action="${employee_skill_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search by name"
                      value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="employee_skill_search_form_results" class="search-results"></ul>
        </div>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Added</th>
            <th>Expires</th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="documents" var="document">
            <s:url action="skills/certificate" var="employee_skill_show_url">
                <s:param name="id">${document.id}</s:param>
            </s:url>

            <tr>
                <td>
                    <a href="${employee_skill_show_url}">${document.name}</a>
                </td>
                <td>${document.startDate}</td>
                <td>
                    <s:if test="#document.doesNotExpire">
                        Never
                    </s:if>
                    <s:else>
                        ${document.endDate}
                    </s:else>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</div>