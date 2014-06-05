<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee/import-export" var="contractor_employee_import_export_url"/>
<s:url action="employee/import" var="contractor_employee_import_url"/>
<s:url action="employee/export" var="contractor_employee_export_url"/>
<s:url action="employee/import-template" var="contractor_employee_import_template_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Import / Export Employee List</s:param>
</s:include>

<div class="row">
    <div class="col-md-6">
        <section class="employee-guard-section">
            <h1><i class="icon-upload"></i> Import</h1>

            <div class="content">
                <div class="alert alert-info clearfix">
                    <h4>Use the template</h4>

                    <p>
                        For the cleanest and most accurate imports, make sure you follow the template's format exactly. First name, last name, and email are required for all employees.
                    </p>

                    <a href="${contractor_employee_import_template_url}" class="btn btn-info">Download Employee Import Template</a>
                </div>

                <tw:form formName="contractor_employee_import" action="${contractor_employee_import_url}" method="POST"
                         enctype="multipart/form-data" role="form">
                    <p class="default-file-import">Import Employees</p>
                    <tw:input type="file" name="upload" class="default-file-import" />
                    <tw:button type="button" class="btn btn-default btn-import">Import Employees</tw:button>
                </tw:form>

                <s:if test="hasActionErrors()">
                    <div class="alert alert-danger">
                        <h4>Import Error</h4>
                        <s:iterator value="actionErrors">
                            <p><s:property escape="false"/></p>
                        </s:iterator>
                    </div>
                </s:if>

                <s:if test="hasActionMessages()">
                    <div class="alert alert-success">
                        <h4>Success!</h4>
                        <s:iterator value="actionMessages">
                            <p><s:property escape="false"/></p>
                        </s:iterator>
                    </div>
                </s:if>
            </div>
        </section>
    </div>

    <div class="col-md-6">
        <section class="employee-guard-section">
            <h1><i class="icon-download"></i> Export</h1>

            <div class="content">
                <tw:form formName="contractor_employee_export" action="${contractor_employee_export_url}" method="post">
                <%--Commented out to resuse whem multiple download types avaiable
                    <div class="control-group">
                        <div class="controls">
                            <tw:label class="radio">
                                <tw:input inputName="type" id="contractor_employee_export_type_pdf" type="radio"
                                          value="PDF"/> PDF
                            </tw:label>
                            <tw:label class="radio">
                                <tw:input inputName="type" id="contractor_employee_export_type_csv" type="radio"
                                          value="CSV" checked="checked"/> Spreadsheet
                            </tw:label>
                        </div>
                    </div> --%>
                    <p>Export your employees as a spreadsheet.</p>
                    <div class="control-group">
                        <div class="controls">
                            <tw:button type="submit" class="btn btn-default">Export Employees</tw:button>
                        </div>
                    </div>
                </tw:form>
            </div>
        </section>
    </div>
</div>
