<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="operator_project_list_url"/>
<s:url action="project" method="create" var="operator_project_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Add Project</s:param>
</s:include>

<div class="col-md-8">
    <tw:form formName="operator_project_create" action="${operator_project_create_url}" method="post"
             class="form-horizontal js-validation">
        <s:if test="permissions.operator">
            <input name="operator_project_create.siteId" type="hidden" value="${permissions.accountId}"/>
        </s:if>

        <fieldset>
            <s:if test="permissions.corporate">
                <div class="form-group">
                    <tw:label labelName="name" class="col-md-3 control-label"><strong>Site</strong></tw:label>
                    <div class="col-md-4">
                        <tw:select selectName="siteId" class="form-control select2" autofocus="true">
                            <s:iterator value="projectSites" var="project_site">
                                <tw:option value="${project_site.id}">${project_site.name}</tw:option>
                            </s:iterator>
                        </tw:select>
                    </div>
                </div>
            </s:if>

            <div class="form-group">
                <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
                <div class="col-md-4">
                    <tw:input inputName="name" class="form-control" type="text"/>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="location" class="col-md-3 control-label">Location</tw:label>
                <div class="col-md-4">
                    <tw:input inputName="location" class="form-control" type="text"/>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="startYear" class="col-md-3 control-label">Start Date</tw:label>
                <div class="col-md-4">
                    <fieldset class="expiration-date">
                        <div class="row date">
                            <div class="col-md-4 col-sm-4 col-xs-6">
                                <tw:input inputName="startYear" type="text" placeholder="YYYY" maxlength="4"
                                          class="form-control year" value="${documentForm.startYear}"/>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="startMonth" type="text" placeholder="MM" maxlength="2"
                                          class="form-control month" value="${documentForm.startMonth}"/>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="startDay" type="text" placeholder="DD" maxlength="2"
                                          class="form-control day" value="${documentForm.startDay}"/>
                            </div>
                            <div class="col-md-1 col-sm-1 col-xs-12">
                                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i
                                        class="icon-calendar"></i></a>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="endYear" class="col-md-3 control-label">End Date</tw:label>
                <div class="col-md-4">
                    <fieldset class="expiration-date">
                        <div class="row date">
                            <div class="col-md-4 col-sm-4 col-xs-6">
                                <tw:input inputName="endYear" type="text" placeholder="YYYY" maxlength="4"
                                          class="form-control year" value="${documentForm.endYear}"/>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="endMonth" type="text" placeholder="MM" maxlength="2"
                                          class="form-control month" value="${documentForm.endMonth}"/>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <tw:input inputName="endDay" type="text" placeholder="DD" maxlength="2"
                                          class="form-control day" value="${documentForm.endDay}"/>
                            </div>
                            <div class="col-md-1 col-sm-1 col-xs-12">
                                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i
                                        class="icon-calendar"></i></a>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="skills" class="col-md-3 control-label">Project Skills</tw:label>
                <div class="col-md-4">
                    <s:set var="selected_skills" value="%{projectForm.skills}"/>
                    <tw:select selectName="skills" multiple="true" class="form-control select2" tabindex="2">
                        <s:iterator value="projectSkills" var="operator_role">
                            <s:set var="is_selected" value="false"/>
                            <s:iterator value="#selected_skills" var="selected_skill">
                                <s:if test="#selected_skill == #operator_role.id">
                                    <s:set var="is_selected" value="true"/>
                                </s:if>
                            </s:iterator>

                            <tw:option value="#operator_role.id"
                                       selected="${is_selected}">${operator_role.name}</tw:option>
                        </s:iterator>
                    </tw:select>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="roles" class="col-md-3 control-label">Job Roles</tw:label>
                <div class="col-md-4">
                    <s:set var="selected_roles" value="%{projectForm.roles}"/>
                    <tw:select selectName="roles" multiple="true" class="form-control select2" tabindex="2">
                        <s:iterator value="projectRoles" var="operator_role">
                            <s:set var="is_selected" value="false"/>
                            <s:iterator value="#selected_roles" var="selected_role">
                                <s:if test="#selected_role == #operator_role.name">
                                    <s:set var="is_selected" value="true"/>
                                </s:if>
                            </s:iterator>

                            <tw:option value="#operator_role.name"
                                       selected="${is_selected}">${operator_role.name}</tw:option>
                        </s:iterator>
                    </tw:select>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-4 col-md-offset-3">
                    <div classs="checkbox">
                        <tw:label labelName="addAnother" class="control-label">
                            <tw:input inputName="addAnother" type="checkbox" value="true" tabindex="4"/> Add Another
                        </tw:label>
                    </div>
                </div>
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="5">Add</tw:button>
                    <a href="${operator_project_list_url}" class="btn btn-default" tabindex="6">Cancel</a>
                </div>
            </div>
        </fieldset>
    </tw:form>
</div>