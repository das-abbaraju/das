<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skills/certificate" method="edit" var="employee_skill_edit_url">
    <s:param name="id">${document.id}</s:param>
</s:url>
<s:url action="skills/certificate" method="create" var="employee_skill_create_url" />

<tw:form formName="employee_skill_edit" action="${employee_skill_edit_url}" method="post" class="form-horizontal js-validation" enctype="multipart/form-data">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Site</strong></tw:label>
            <div class="col-md-4">
                <tw:select selectName="documentId" class="form-control">
                    <s:iterator value="documents" var="document">
                        <tw:option value="${document.id}">${document.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${document.name}" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label">Location</tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${document.name}" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="expireYear" class="col-md-3 control-label">Start Date</tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="expireYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" value="${documentForm.expireYear}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" value="${documentForm.expireMonth}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireDay" type="text" placeholder="DD" maxlength="2" class="form-control day" value="${documentForm.expireDay}" />
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-12">
                            <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="expireYear" class="col-md-3 control-label">End Date</tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="expireYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" value="${documentForm.expireYear}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" value="${documentForm.expireMonth}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireDay" type="text" placeholder="DD" maxlength="2" class="form-control day" value="${documentForm.expireDay}" />
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-12">
                            <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel">Cancel</tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>
