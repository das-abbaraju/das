<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" method="edit" var="operator_project_roles_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="operator_project_roles_edit" action="${operator_project_roles_edit_url}" method="post"
         class="form-horizontal" role="form">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="roles" class="col-md-3 control-label">Job Roles</tw:label>
            <div class="col-md-4">
                <s:set var="selected_roles" value="%{projectRolesForm.roles}"/>
                <tw:select selectName="roles" multiple="true" class="form-control select2" autofocus="true" tabindex="1">
                    <s:iterator value="projectRoles" var="project_role">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_roles" var="selected_role">
                            <s:if test="#selected_role == #project_role.name">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="${project_role.name}"
                                   selected="${is_selected}">${project_role.name}</tw:option>
                    </s:iterator>
                </tw:select>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="2">Save</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="3">Cancel</tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>