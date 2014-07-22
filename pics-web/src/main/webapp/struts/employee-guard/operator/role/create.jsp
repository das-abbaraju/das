<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" var="operator_role_list_url" />
<s:url action="role" method="create" var="operator_role_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="OPERATOR.JOB_ROLES.ADD.PAGE.HEADER"/></s:param>
</s:include>

<div class="col-md-8">
    <tw:form formName="operator_role_create" action="${operator_role_create_url}" method="post" class="form-horizontal js-validation" role="form">
        <fieldset>
            <div class="form-group">
                <tw:label labelName="name" class="col-md-3 control-label"><strong><s:text name="OPERATOR.JOB_ROLES.ADD.NAME"/></strong></tw:label>
                <div class="col-md-4">
                    <tw:input inputName="name" class="form-control" tabindex="1" type="text" autofocus="true" maxlength="70" />
                </div>
            </div>

            <s:set var="selected_skills" value="roleForm.skills"/>

            <div class="form-group">
                <tw:label labelName="skills" class="col-md-3 control-label"><s:text name="OPERATOR.JOB_ROLES.ADD.REQUIRED_SKILLS"/></tw:label>
                <div class="col-md-4">
                    <tw:select selectName="skills" multiple="true" class="form-control select2" tabindex="2">
                        <s:iterator value="roleSkills" var="company_skill">
                            <s:set var="is_selected" value="false"/>
                            <s:iterator value="#selected_skills" var="selected_skill">
                                <s:if test="#selected_skill == #company_skill.id">
                                    <s:set var="is_selected" value="true"/>
                                </s:if>
                            </s:iterator>

                            <tw:option value="#company_skill.id" selected="${is_selected}">${company_skill.name}</tw:option>
                        </s:iterator>
                    </tw:select>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-4 col-md-offset-3">
                    <div classs="checkbox">
                        <tw:label labelName="addAnother" class="control-label">
                            <tw:input inputName="addAnother" type="checkbox" value="true" tabindex="4"/> <s:text name="OPERATOR.JOB_ROLES.ADD.ADD_ANOTHER.CHECKBOX"/>
                        </tw:label>
                    </div>
                </div>
                <div class="col-md-9 col-md-offset-3 form-actions">
                    <tw:button buttonName="save" type="submit" class="btn btn-success" tabindex="5"><s:text name="OPERATOR.JOB_ROLES.ADD.ADD.BUTTON"/></tw:button>
                    <a href="${operator_role_list_url}" class="btn btn-default" tabindex="6"><s:text name="OPERATOR.JOB_ROLES.ADD.CANCEL.BUTTON"/></a>
                </div>
            </div>
        </fieldset>
    </tw:form>
</div>