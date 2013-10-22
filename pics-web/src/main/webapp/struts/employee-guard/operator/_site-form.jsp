<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>

<tw:form formName="contractor_employee_edit" action="${contractor_employee_edit_url}" method="post" class="form-horizontal js-validation" autocomplete="off" role="form">
    <div class="form-group">
        <tw:label labelName="skills" class="col-md-3 control-label">Requssired Skills</tw:label>
        <div class="col-md-4">
            <tw:select selectName="skills" multiple="true" class="form-control" tabindex="2">
                <s:iterator value="groupSkills" var="company_skill">
                    <s:set var="is_selected" value="false"/>
                    <s:iterator value="#selected_skills" var="selected_skill">
                        <s:if test="#selected_skill.skill.id == #company_skill.id">
                            <s:set var="is_selected" value="true"/>
                        </s:if>
                    </s:iterator>

                    <tw:option value="#company_skill.id" selected="${is_selected}">${company_skill.name}</tw:option>
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
</tw:form>