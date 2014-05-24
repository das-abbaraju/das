<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" var="employee_file_list_url"/>
<s:url action="file" method="create" var="employee_file_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">My Files</s:param>
    <s:param name="actions">
        <a href="${employee_file_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> File</a>
    </s:param>
</s:include>


<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp"/>


<s:if test="documents.size() > 0">
    <tw:form formName="employee_skill_search" action="${employee_skill_list_url}" class="search-query" role="form">
        <fieldset>
            <div class="search-wrapper col-md-4">
                <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search My Files"
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
                    <s:url action="file" var="employee_file_show_url">
                        <s:param name="id">${document.id}</s:param>
                    </s:url>

                    <s:if test="#document.status.displayValue == 'expired'">
                        <s:set var="document_status_class">danger</s:set>
                    </s:if>
                    <s:elseif test="#document.status.displayValue == 'expiring'">
                        <s:set var="document_status_class">warning</s:set>
                    </s:elseif>
                    <s:else>
                        <s:set var="document_status_class">success</s:set>
                    </s:else>

                    <s:if test="#document.doesNotExpire">
                        <s:set var="document_expires">Never</s:set>
                    </s:if>
                    <s:else>
                        <s:set var="document_expires">${document.expires}</s:set>
                    </s:else>
                    <tr>
                        <td>
                            <a href="${employee_file_show_url}">${document.name}</a>
                        </td>
                        <td>${document.added}</td>
                        <td class="${document_status_class}">${document_expires}</td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</s:if>
<s:else>
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="alert alert-info">
                <h4>No Files</h4>

                <p>Add certificates or other skill proof by selecting <strong><i class="icon-plus-sign"></i> File</strong> at the top of the page. Upload any certificates, files, or photos you already have; then easily apply those uploads to your required skills!</p>

                <p>
                    <a href="#"><i class="icon-question-sign"></i> Learn more about My Files</a>
                </p>
            </div>
        </div>
    </div>
</s:else>