<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" method="edit" var="operator_project_companies_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="operator_project_companies_edit" action="${operator_project_companies_edit_url}" method="post"
         class="form-horizontal" autocomplete="off" role="form">
    <s:set var="selected_groups" value="employee.groups"/>

    <div class="form-group">
        <tw:label labelName="companies" class="col-md-3 control-label">Companies</tw:label>
        <div class="col-md-4">
            <s:set var="selected_companies" value="projectCompaniesForm.companies" />
            <tw:select selectName="companies" multiple="true" class="form-control select2" tabindex="3">
                <s:iterator value="projectSites" var="project_company">
                    <s:set var="is_selected" value="false"/>
                    <s:iterator value="#selected_companies" var="selected_company">
                        <s:if test="#selected_company == #project_company.id">
                            <s:set var="is_selected" value="true"/>
                        </s:if>
                    </s:iterator>

                    <tw:option value="${project_company.id}" selected="${is_selected}">${project_company.name}</tw:option>
                </s:iterator>
            </tw:select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-9 col-md-offset-3 form-actions">
            <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="4">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="5">Cancel</tw:button>
        </div>
    </div>
</tw:form>