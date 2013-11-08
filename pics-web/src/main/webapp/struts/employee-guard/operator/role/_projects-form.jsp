<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" method="edit" var="operator_role_project_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="operator_role_project_edit" action="${operator_role_project_edit_url}" method="post" class="form-horizontal" role="form">
    <fieldset>
        <s:set var="selected_projects" value="role.projects"/>

        <div class="form-group">
            <tw:select selectName="projects" multiple="true" class="form-control select2" autofocus="true" tabindex="1">
                <s:iterator value="operatorProjects" var="operator_project">
                    <s:set var="is_selected" value="false"/>
                    <s:iterator value="#selected_projects" var="selected_project">
                        <s:if test="#selected_project.project.id == #operator_project.id">
                            <s:set var="is_selected" value="true"/>
                        </s:if>
                    </s:iterator>
                    <tw:option value="${operator_project.id}" selected="${is_selected}">${operator_project.name}</tw:option>

                </s:iterator>
            </tw:select>
        </div>

        <div class="form-group">
            <tw:button buttonName="save" type="submit" class="btn btn-success btn-block" tabindex="2">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default btn-block cancel" tabindex="3">Cancel</tw:button>
        </div>
    </fieldset>

</tw:form>