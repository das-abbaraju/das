<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" method="edit" var="operator_project_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>

<tw:form formName="operator_project_name_skills_edit" action="${operator_project_edit_url}" method="post"
         class="form-horizontal js-validation">
    <fieldset>
        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label">Site</tw:label>
            <div class="col-md-4">
                <tw:input inputName="site" class="form-control disabled" disabled="true" value="${projectNameSkillsForm.site}" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong>Name</strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${projectNameSkillsForm.name}"/>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="location" class="col-md-3 control-label">Location</tw:label>
            <div class="col-md-4">
                <tw:input inputName="location" class="form-control" type="text"
                          value="${projectNameSkillsForm.location}"/>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="startYear" class="col-md-3 control-label">Start Date</tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="startYear" type="text" placeholder="YYYY" maxlength="4"
                                      class="form-control year"
                                      value="${projectNameSkillsForm.startYear > 0 ? projectNameSkillsForm.startYear : ''}"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="startMonth" type="text" placeholder="MM" maxlength="2"
                                      class="form-control month"
                                      value="${projectNameSkillsForm.startMonth > 0 ? projectNameSkillsForm.startMonth : ''}"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="startDay" type="text" placeholder="DD" maxlength="2"
                                      class="form-control day"
                                      value="${projectNameSkillsForm.startDay > 0 ? projectNameSkillsForm.startDay : ''}"/>
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
                                      class="form-control year"
                                      value="${projectNameSkillsForm.endYear > 0 ? projectNameSkillsForm.endYear : ''}"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="endMonth" type="text" placeholder="MM" maxlength="2"
                                      class="form-control month"
                                      value="${projectNameSkillsForm.endMonth > 0 ? projectNameSkillsForm.endMonth : ''}"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="endDay" type="text" placeholder="DD" maxlength="2"
                                      class="form-control day"
                                      value="${projectNameSkillsForm.endDay > 0 ? projectNameSkillsForm.endDay : ''}"/>
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
                <s:set var="selected_skills" value="%{projectNameSkillsForm.skills}"/>
                <tw:select selectName="skills" multiple="true" class="form-control select2" tabindex="2">
                    <s:iterator value="projectSkills" var="operator_role">
                        <s:set var="is_selected" value="false"/>
                        <s:iterator value="#selected_skills" var="selected_skill">
                            <s:if test="#selected_skill == #operator_role.id">
                                <s:set var="is_selected" value="true"/>
                            </s:if>
                        </s:iterator>

                        <tw:option value="#operator_role.id" selected="${is_selected}">${operator_role.name}</tw:option>
                    </s:iterator>
                </tw:select>
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
