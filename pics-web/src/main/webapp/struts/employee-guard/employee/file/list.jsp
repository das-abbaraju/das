<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" var="employee_file_list_url"/>
<s:url action="file" method="create" var="employee_file_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="EMPLOYEE.FILE.LIST.HEADER"/></s:param>
    <s:param name="actions">
        <a href="${employee_file_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> <s:text name="EMPLOYEE.FILE.LIST.FILE.BUTTON"/></a>
    </s:param>
</s:include>

<s:if test="documents.size() > 0">
    <tw:form formName="employee_skill_search" action="${employee_skill_list_url}" class="search-query" role="form">
        <fieldset>
            <div class="search-wrapper col-md-4">
                <s:set var="placeholderSearchMyFiles"><s:text name="EMPLOYEE.FILE.LIST.SEARCH.DEFAULT_TEXT"/></s:set>
                <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="${placeholderSearchMyFiles}"
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
                    <th><s:text name="EMPLOYEE.FILE.LIST.TABLE.NAME.COLUMN"/></th>
                    <th><s:text name="EMPLOYEE.FILE.LIST.TABLE.ADDED.COLUMN"/></th>
                    <th><s:text name="EMPLOYEE.FILE.LIST.TABLE.EXPIRES.COLUMN"/></th>
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
                        <s:set var="document_expires"><s:text name="EMPLOYEE.FILE.LIST.TABLE.EXPIRES.COLUMN.NEVER_EXPIRES"/></s:set>
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
                <h4><s:text name="EMPLOYEE.FILE.LIST.TABLE.NO_FILES_MSG.TITLE"/></h4>

                <p><s:text name="EMPLOYEE.FILE.LIST.TABLE.NO_FILES_MSG.MSG1"/></p>

                <p>
                    <a href="#"><i class="icon-question-sign"></i> <s:text name="EMPLOYEE.FILE.LIST.TABLE.NO_FILES_MSG.MSG2"/></a>
                </p>
            </div>
        </div>
    </div>
</s:else>