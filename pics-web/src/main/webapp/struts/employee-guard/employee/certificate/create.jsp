<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skills/certificate" var="employee_skill_list_url" />
<s:url action="skills/certificate" method="create" var="employee_skill_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Add Certificate</s:param>
</s:include>

<div class="col-md-8">
    <tw:form formName="employee_skill_create" action="${employee_skill_create_url}" method="post" class="form-horizontal js-validation" enctype="multipart/form-data">
        <fieldset>
            <div class="form-group">
                <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
                <div class="col-md-4">
                    <tw:input inputName="name" class="form-control" type="text" />
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="file" class="col-md-3 control-label"><strong>Proof</strong></tw:label>
                <div class="col-md-9">
                    <tw:input inputName="file" type="file" class="file-import display-file-import" />
                    <tw:button type="button" class="btn btn-default btn-import">
                        <i class="icon-upload"></i>Upload
                    </tw:button>
                </div>
                <div class="col-md-9 col-md-offset-3 filename-display"></div>
            </div>

            <div class="form-group">
                <tw:label labelName="expireYear" class="col-md-3 control-label"><strong>Expires</strong></tw:label>
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
                            <tw:input inputName="noExpiration" type="checkbox" class="no-expiration" value="true" /> Does not expire
                        </tw:label>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                    <a href="${employee_skill_list_url}" class="btn btn-default">Cancel</a>
                </div>
            </div>
        </fieldset>
    </tw:form>
</div>