<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" var="contractor_employee_list_url"/>
<s:url action="employee" method="create" var="contractor_employee_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="EMPLOYEE.CREATE.ADD_EMPLOYEE" /></s:param>
</s:include>

<tw:form formName="contractor_employee_create" action="${contractor_employee_create_url}" method="post" class="form-horizontal js-validation" enctype="multipart/form-data" autocomplete="off" role="form">
    <fieldset>
        <div class="row">
            <div class="col-md-3">
                <s:include value="/struts/employee-guard/employee/photo/_photo.jsp">
                    <s:url action="employee" method="photo" var="image_url">
                        <s:param name="id">0</s:param>
                    </s:url>
                    <s:set var="alt_text"><s:text name="EMPLOYEE.CREATE.PROFILE_PHOTO" /></s:set>
                </s:include>
            </div>

            <div class="col-md-9">
                <div class="form-group">
                    <tw:label labelName="firstName" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.CREATE.FIRST_NAME" /></strong></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="firstName" class="form-control" tabindex="1" type="text" autofocus="true" maxlength="100" />
                    </div>
                </div>

                <div class="form-group">
                    <tw:label labelName="lastName" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.CREATE.LAST_NAME" /></strong></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="lastName" class="form-control" tabindex="2" type="text" maxlength="100" />
                    </div>
                </div>

                <div class="form-group ${email_error_class}">
                    <tw:label labelName="email" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.CREATE.EMAIL" /></strong></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="email" class="form-control" tabindex="3" type="text" maxlength="70" />
                        <tw:error errorName="email"/>
                    </div>
                </div>

                <div class="form-group">
                    <tw:label labelName="phoneNumber" class="col-md-3 control-label"><s:text name="EMPLOYEE.CREATE.PHONE" /></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="phoneNumber" class="form-control" tabindex="4" type="text" maxlength="24" />
                    </div>
                </div>

                <div class="form-group">
                    <tw:label labelName="employeeId" class="col-md-3 control-label"><s:text name="EMPLOYEE.CREATE.EMPLOYEE_ID" /></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="employeeId" class="form-control" tabindex="5" type="text" maxlength="70" />
                    </div>
                </div>

                <div class="form-group">
                    <tw:label labelName="title" class="col-md-3 control-label"><s:text name="EMPLOYEE.CREATE.TITLE" /></tw:label>
                    <div class="col-md-4">
                        <tw:input inputName="title" class="form-control" tabindex="6" type="text" maxlength="100" />
                    </div>
                </div>

                <s:set var="selected_groups" value="employeeForm.groups"/>

                <div class="form-group">
                    <tw:label labelName="groups" class="col-md-3 control-label"><s:text name="EMPLOYEE.CREATE.EMPLOYEE_GROUPS" /></tw:label>
                    <div class="col-md-4">
                        <tw:select selectName="groups" class="form-control select2" tabindex="7" multiple="true">
                            <s:iterator value="employeeGroups" var="contractor_group">
                                <s:set var="is_selected" value="false"/>
                                <s:iterator value="#selected_groups" var="selected_group">
                                    <s:if test="#selected_group == #contractor_group.name">
                                        <s:set var="is_selected" value="true"/>
                                    </s:if>
                                </s:iterator>

                                <tw:option value="${contractor_group.name}" selected="${is_selected}">${contractor_group.name}</tw:option>
                            </s:iterator>
                        </tw:select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-4 col-md-offset-3">
                        <div class="checkbox">
                            <tw:label labelName="addAnother" class="control-label">
                                <tw:input inputName="addAnother" type="checkbox" tabindex="8" value="true"/><s:text name="EMPLOYEE.CREATE.ADD_ANOTHER" />
                            </tw:label>
                        </div>
                    </div>
                    <div class="col-md-9 col-md-offset-3 form-actions">
                        <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="9"><s:text name="EMPLOYEE.CREATE.ADD" /></tw:button>
                        <a href="${contractor_employee_list_url}" class="btn btn-default" tabindex="10"><s:text name="EMPLOYEE.CREATE.CANCEL" /></a>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</tw:form>