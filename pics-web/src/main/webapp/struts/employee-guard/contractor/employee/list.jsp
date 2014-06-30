<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" var="contractor_employee_list_url"/>
<s:url action="employee" method="create" var="contractor_employee_create_url"/>
<s:url action="employee/import-export" var="contractor_employee_import_export_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Employees</s:param>
    <s:param name="actions">
        <a href="${contractor_employee_import_export_url}" class="btn btn-default">Import / Export</a>
        <a href="${contractor_employee_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Employee</a>
    </s:param>
</s:include>

<s:if test="employees.size() > 0">
    <tw:form formName="contractor_employee_search" action="${contractor_employee_list_url}" class="search-query"
             role="form">
        <fieldset>
            <div class="search-wrapper col-md-4">
                <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Employees"
                          value="${searchForm.searchTerm}"/>
                <i class="icon-search"></i>
                <ul id="contractor_employee_search_form_results" class="search-results"></ul>
            </div>
        </fieldset>
    </tw:form>

    <div class="table-responsive">
        <table class="table table-striped table-condensed table-hover">
            <thead>
                <tr>
                    <th class="danger text-center">
                        <i class="icon-minus-sign-alt icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Expired or Incomplete"></i>
                    </th>
                    <th class="warning text-center">
                        <i class="icon-warning-sign icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Expiring"></i>
                    </th>
                    <th class="employee">Employee</th>
                    <th class="title">Title</th>
                    <th class="groups">Employee Groups</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="employees" var="employee">
                    <s:url action="employee" var="contractor_employee_show_url">
                        <s:param name="id">${employee.id}</s:param>
                    </s:url>

                    <%-- TODO: Please clean this up --%>
                    <s:set var="incomplete_status">${employeeSkillStatuses.get(employee, 'expired')}</s:set>
                    <s:set var="expiring_status">${employeeSkillStatuses.get(employee, 'expiring')}</s:set>

                    <s:if test="#incomplete_status > 0">
                        <s:set var="incomplete_class">danger</s:set>
                    </s:if>
                    <s:else>
                        <s:set var="incomplete_class" value="" />
                    </s:else>

                    <s:if test="#expiring_status > 0">
                        <s:set var="expiring_class">warning</s:set>
                    </s:if>
                    <s:else>
                        <s:set var="expiring_class" value="" />
                    </s:else>

                    <tr>
                        <td class="${incomplete_class} text-center">
                            <s:if test="#incomplete_status > 0">
                                ${incomplete_status}
                            </s:if>
                        </td>
                        <td class="${expiring_class} text-center">
                            <s:if test="#expiring_status > 0">
                                ${expiring_status}
                            </s:if>
                        </td>
                        <td>
                            <a href="${contractor_employee_show_url}">${employee.firstName} ${employee.lastName}</a>
                        </td>
                        <td>${employee.positionName}</td>
                        <td>
                            <s:set var="contractor_groups" value="#employee.groups"/>
                            <s:include value="/struts/employee-guard/contractor/group/_list.jsp"/>
                        </td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</s:if>
<s:else>
    <section class="employee-guard-section">
        <h1>
            <i class="icon-map-marker icon-large"></i>Employees
        </h1>
        <div class="content">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="alert alert-info">
                        <h4>No Employees</h4>

                        <p>Add your employees either individually by selecting <strong><i class="icon-plus-sign"></i> Employee</strong>, or import a batch by selecting <strong>Import/Export</strong> at the top of the page.</p>

                        <p>After your employees are created you can manage and monitor them on this page.</p>

                        <p>
                            <a href="#"><i class="icon-question-sign"></i> Learn more about Adding Employees</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>
</s:else>