<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" method="edit" var="employee_file_edit_url">
    <s:param name="id">${document.id}</s:param>
</s:url>

<tw:form formName="employee_file_edit" action="${employee_file_edit_url}" method="post" class="form-horizontal js-validation disable-on-submit" enctype="multipart/form-data">
    <tw:input type="hidden" inputName="id" value="${document.id}" />
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${document.name}" maxlength="70" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="file" class="col-md-3 control-label"><strong>File</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="file" type="file" class="file-import default-file-import col-md-12" />
                <tw:input id="validate-filename" inputName="validate_filename" value="${document.fileName}" type="hidden" />

                <tw:button type="button" class="btn btn-default btn-import">
                    <i class="icon-upload"></i>Upload New
                </tw:button>
                <div class="row">
                    <div class="col-md-12">
                        <span class="filename-display">${document.fileName}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="expireYear" class="col-md-3 control-label"><strong>Expires</strong></tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="expireYear" type="text" placeholder="YYYY" maxlength="4" class="form-control year" value="${documentForm.expireYear > 0 ? documentForm.expireYear : ''}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireMonth" type="text" placeholder="MM" maxlength="2" class="form-control month" value="${documentForm.expireMonth > 0 ? documentForm.expireMonth : ''}" />
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="expireDay" type="text" placeholder="DD" maxlength="2" class="form-control day" value="${documentForm.expireDay > 0 ? documentForm.expireDay : ''}" />
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-12">
                            <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
                        </div>
                    </div>
                </fieldset>
                <div class="checkbox">
                    <tw:label labelName="noExpiration" class="control-label">
                        <s:if test="documentForm.noExpiration">
                            <tw:input inputName="noExpiration" type="checkbox" class="no-expiration" data-toggle="form-input" data-target=".expiration-date" value="true" checked="checked" />
                        </s:if>
                        <s:else>
                            <tw:input inputName="noExpiration" type="checkbox" class="no-expiration" data-toggle="form-input" data-target=".expiration-date" value="true" />
                        </s:else>
                        Does not expire
                    </tw:label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="update" type="submit" class="btn btn-primary">Update</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel">Cancel</tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>
