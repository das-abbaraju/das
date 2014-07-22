<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="file" method="create" var="employee_file_create_url" />
<s:url action="skill" method="edit" var="employee_skill_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skills" var="employee_skills_list_url" />

<div class="row">
  <div class="col-md-8">
    <tw:form formName="employee_skill_file" action="${employee_skill_url}" method="post" enctype="multipart/form-data" class="form-horizontal js-validation" role="form">
        <fieldset>
            <div class="form-group">
                <tw:label labelName="documentId" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.FILE.UPLOAD.FILE.LABEL"/></strong></tw:label>
                <div class="col-md-4">
                    <tw:select selectName="documentId" class="form-control select2 file-select">
                        <tw:option value="null"><s:text name="EMPLOYEE.FILE.UPLOAD.MULTISELECT.NONE.OPTION"/></tw:option>
                        <s:iterator value="documents" var="document">
                            <s:if test="%{#document.id== documentId}">
                              <tw:option value="${document.id}" selected="true">${document.name}</tw:option>
                            </s:if>
                            <s:else>
                              <tw:option value="${document.id}" >${document.name}</tw:option>
                            </s:else>
                        </s:iterator>
                    </tw:select>
                </div>
                <div class="col-md-2">
                    <s:url action="file" var="employee_file_edit_url">
                        <s:param name="id">${documentId}</s:param>
                    </s:url>

                    <a href="${employee_file_edit_url}" class="btn btn-link edit-btn btn-auto-width">
                        <i class="icon-edit icon-large"></i>
                    </a>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-4 col-md-offset-3">
                    <a href="${employee_file_create_url}" class="btn btn-default btn-auto-width"><i class="icon-plus-sign"></i> <s:text name="EMPLOYEE.FILE.UPLOAD.FILE.LINK"/></a>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <tw:button buttonName="update" type="submit" disabled="disabled" class="btn btn-primary"><s:text name="EMPLOYEE.FILE.UPLOAD.UPDATE.BUTTON"/></tw:button>
                    <tw:button buttonName="cancel" type="button" class="btn btn-default cancel"><s:text name="EMPLOYEE.FILE.UPLOAD.CANCEL.BUTTON"/></tw:button>
                </div>
            </div>
        </fieldset>
    </tw:form>
  </div>
</div>
