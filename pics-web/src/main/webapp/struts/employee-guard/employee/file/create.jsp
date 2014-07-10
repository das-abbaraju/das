<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" var="employee_skill_list_url" />
<s:url action="file" method="create" var="employee_file_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="EMPLOYEE.FILE.ADD.HEADER"/></s:param>
</s:include>

<div class="col-md-8">
    <tw:form formName="employee_file_create" action="${employee_file_create_url}" method="post" class="form-horizontal js-validation disable-on-submit" enctype="multipart/form-data">
        <fieldset>
            <div class="form-group">
                <tw:label labelName="name" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.FILE.ADD.NAME"/></strong></tw:label>
                <div class="col-md-4">
                    <tw:input inputName="name" class="form-control" type="text" maxlength="70" />
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="file" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.FILE.ADD.FILE"/></strong></tw:label>
                <div class="col-md-4">
                    <tw:input inputName="file" type="file" class="file-import default-file-import" />
                    <tw:input id="validate-filename" inputName="validate_filename" type="hidden" />

                    <tw:button type="button" class="btn btn-default btn-import">
                        <i class="icon-upload"></i><s:text name="EMPLOYEE.FILE.ADD.UPLOAD"/>
                    </tw:button>
                    <div class="row">
                        <div class="col-md-12">
                            <span class="filename-display"></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="expireYear" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.FILE.ADD.EXPIRES"/></strong></tw:label>
                <div class="col-md-4">
                    <fieldset class="expiration-date">
                        <div class="row date">
                            <div class="col-md-4 col-sm-4 col-xs-6">
                                <tw:input inputName="expireYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="expireMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" />
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="expireDay" type="text" placeholder="DD" maxlength="2" class="form-control day" />
                            </div>
                            <div class="col-md-1 col-sm-1 col-xs-12">
                                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
                            </div>
                        </div>
                    </fieldset>
                    <div class="checkbox">
                        <tw:label labelName="noExpiration" class="control-label">
                            <tw:input inputName="noExpiration" type="checkbox" class="no-expiration" data-toggle="form-input" data-target=".expiration-date" value="true" /> <s:text name="EMPLOYEE.FILE.ADD.DOES_NOT_EXPIRE.CHECKBOX"/>
                        </tw:label>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <tw:button buttonName="save" type="submit" class="btn btn-success"><s:text name="EMPLOYEE.FILE.ADD.SAVE.BUTTON"/></tw:button>
                    <a href="${employee_skill_list_url}" class="btn btn-default"><s:text name="EMPLOYEE.FILE.ADD.CANCEL.BUTTON"/></a>
                </div>
            </div>
        </fieldset>
    </tw:form>
</div>