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
            <tw:label labelName="name" class="col-md-3 control-label"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.SITE.LABEL"/></tw:label>
            <div class="col-md-4">
                <input name="operator_project_name_skills_edit.siteId" value="${projectNameSkillsForm.siteId}" type="hidden" />
                <tw:input inputName="site" class="form-control disabled" disabled="true" value="${projectNameSkillsForm.site}" tabindex="1" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="name" class="col-md-3 control-label"><strong><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.NAME.LABEL"/></strong></tw:label>
            <div class="col-md-4">
                <tw:input inputName="name" class="form-control" type="text" value="${projectNameSkillsForm.name}" tabindex="2" autofocus="true" maxlength="70" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="location" class="col-md-3 control-label"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.LOCATION.LABEL"/></tw:label>
            <div class="col-md-4">
                <tw:input inputName="location" class="form-control" type="text" value="${projectNameSkillsForm.location}" tabindex="3" maxlength="70" />
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="startYear" class="col-md-3 control-label"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.START_DATE.LABEL"/></tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="startYear" type="text" placeholder="YYYY" maxlength="4"
                                      class="form-control year"
                                      value="${projectNameSkillsForm.startYear > 0 ? projectNameSkillsForm.startYear : ''}" tabindex="4"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="startMonth" type="text" placeholder="MM" maxlength="2"
                                      class="form-control month"
                                      value="${projectNameSkillsForm.startMonth > 0 ? projectNameSkillsForm.startMonth : ''}" tabindex="5"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="startDay" type="text" placeholder="DD" maxlength="2"
                                      class="form-control day"
                                      value="${projectNameSkillsForm.startDay > 0 ? projectNameSkillsForm.startDay : ''}" tabindex="6"/>
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-12">
                            <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i
                                    class="icon-calendar" tabindex="7"></i></a>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="endYear" class="col-md-3 control-label"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.END_DATE.LABEL"/></tw:label>
            <div class="col-md-4">
                <fieldset class="expiration-date">
                    <div class="row date">
                        <div class="col-md-4 col-sm-4 col-xs-6">
                            <tw:input inputName="endYear" type="text" placeholder="YYYY" maxlength="4"
                                      class="form-control year"
                                      value="${projectNameSkillsForm.endYear > 0 ? projectNameSkillsForm.endYear : ''}" tabindex="8"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="endMonth" type="text" placeholder="MM" maxlength="2"
                                      class="form-control month"
                                      value="${projectNameSkillsForm.endMonth > 0 ? projectNameSkillsForm.endMonth : ''}" tabindex="9"/>
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            <tw:input inputName="endDay" type="text" placeholder="DD" maxlength="2"
                                      class="form-control day"
                                      value="${projectNameSkillsForm.endDay > 0 ? projectNameSkillsForm.endDay : ''}" tabindex="10"/>
                        </div>
                        <div class="col-md-1 col-sm-1 col-xs-12">
                            <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i
                                    class="icon-calendar" tabindex="11"></i></a>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="skills" class="col-md-3 control-label"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.SKILLS.LABEL"/></tw:label>
            <div class="col-md-4">
                <s:set var="selected_skills" value="%{projectNameSkillsForm.skills}"/>
                <tw:select selectName="skills" multiple="true" class="form-control select2" tabindex="12">
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
                <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="13"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.SAVE.BUTTON"/></tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel" tabindex="14"><s:text name="OPERATOR.PROJECT.PROJECT.EDIT.CANCEL.BUTTON"/></tw:button>
            </div>
        </div>
    </fieldset>
</tw:form>
