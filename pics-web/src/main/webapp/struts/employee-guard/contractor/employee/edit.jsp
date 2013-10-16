<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="first_name_error_class" value="%{hasFieldError('employee.first_name') ? 'error' : ''}" />
<s:set var="last_name_error_class" value="%{hasFieldError('employee.last_name') ? 'error' : ''}" />
<s:set var="email_error_class" value="%{hasFieldError('employee.email') ? 'error' : ''}" />

<%-- Url --%>
<s:url action="employee" var="contractor_employee_show_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>
<s:url action="employee" method="delete" var="contractor_employee_delete_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>
<s:url action="employee" method="deleteConfirmation" var="contractor_employee_delete_confirmation_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Employee</s:param>
    <s:param name="actions">
        <a href="${contractor_employee_delete_url}" class="btn btn-danger delete" data-url="${contractor_employee_delete_confirmation_url}">Delete</a>
    </s:param>
</s:include>

<%-- Breadcrumb --%>
<s:include value="/struts/employee-guard/_breadcrumb.jsp"/>

<tw:form formName="contractor_employee_edit" action="${contractor_employee_edit_url}" method="post" class="form-horizontal" enctype="multipart/form-data" autocomplete="off">
    <fieldset>
        <div class="row">
            <div class="col-md-3">
                <figure class="employee-image img-polaroid">
                    <tw:input inputName="photo" type="file"/>

                    <img src="/v7/img/employee-guard/dummy.jpg" alt="Profile photo" />

                    <div class="overlay-container">
                        <div class="overlay"></div>
                        <span class="edit-text">Select to edit</span>
                    </div>
                </figure>

                <%-- <section class="employee-guard-section">
                    <h1><i class="icon-certificate icon-large"></i> Skills</h1>

                    <ul class="unstyled skill-list">
                        <li class="skill">
                            <div class="complete">
                                <i class="icon-ok-sign"></i> PICS Orientation
                            </div>
                        </li>
                        <li class="skill">
                            <div class="pending">
                                <i class="icon-ok-circle"></i> Harassment Training
                            </div>
                        </li>
                        <li class="skill">
                            <div class="expire">
                                <i class="icon-warning-sign"></i> Creative Suite
                            </div>
                        </li>
                        <li class="skill">
                            <div class="incomplete">
                                <i class="icon-minus-sign-alt"></i> Product Owner Certification
                            </div>
                        </li>
                    </ul>
                </section> --%>
            </div>

            <div class="col-md-9">
                <section class="employee-guard-section">
                    <h1><i class="icon-user icon-large"></i> Personal</h1>

                    <div class="content">
                        <div class="control-group ${first_name_error_class}">
                            <tw:label labelName="firstName"><strong>First Name</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="firstName" type="text" value="${employee.firstName}" />
                                <tw:error errorName="firstName" />
                            </div>
                        </div>

                        <div class="control-group ${last_name_error_class}">
                            <tw:label labelName="lastName"><strong>Last Name</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="lastName" type="text" value="${employee.lastName}" />
                                <tw:error errorName="lastName" />
                            </div>
                        </div>

                        <div class="control-group ${email_error_class}">
                            <tw:label labelName="email"><strong>Email</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="email" type="text" value="${employee.email}" />
                                <tw:error errorName="email" />
                            </div>
                        </div>

                        <div class="control-group">
                            <tw:label labelName="phoneNumber">Phone Number</tw:label>
                            <div class="controls">
                                <tw:input inputName="phoneNumber" type="text" value="${employee.phone}"/>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="employee-guard-section">
                    <h1><i class="icon-file-text-alt icon-large"></i> Employment</h1>

                    <div class="content">
                        <div class="control-group">
                            <tw:label labelName="employeeId">Employee ID</tw:label>
                            <div class="controls">
                                <tw:input inputName="employeeId" type="text" value="${employee.slug}"/>
                            </div>
                        </div>

                        <div class="control-group">
                            <tw:label labelName="title">Title</tw:label>
                            <div class="controls">
                                <tw:input inputName="title" type="text" value="${employee.positionName}"/>
                            </div>
                        </div>

                        <%-- <div class="control-group">
                            <tw:label labelName="classification">Classification</tw:label>
                            <div class="controls">
                                <tw:select selectName="classification">
                                    <s:iterator value="@com.picsauditing.employeeguard.entities.PositionType@values()" var="positionType">
                                        <tw:option value="${positionType.dbValue}">${positionType.displayName}</tw:option>
                                    </s:iterator>
                                </tw:select>
                            </div>
                        </div> --%>

                        <s:set var="selected_groups" value="employeeForm.groups"/>

                        <div class="control-group">
                            <tw:label labelName="groups">Employee Groups</tw:label>
                            <div class="controls">
                                <tw:select selectName="groups" multiple="true">
                                    <s:iterator value="employeeGroups" var="contractor_group">
                                        <s:set var="is_selected" value="false" />
                                        <s:iterator value="#selected_groups" var="selected_group">
                                            <s:if test="#selected_group == #contractor_group.name">
                                                <s:set var="is_selected" value="true" />
                                            </s:if>
                                        </s:iterator>

                                        <tw:option value="${contractor_group.name}" selected="${is_selected}">${contractor_group.name}</tw:option>
                                    </s:iterator>
                                </tw:select>
                            </div>
                        </div>
                    </div>
                </section>

                <div class="control-group">
                    <div class="controls">
                        <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                        <a href="${contractor_employee_show_url}" class="btn btn-default">Cancel</a>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</tw:form>