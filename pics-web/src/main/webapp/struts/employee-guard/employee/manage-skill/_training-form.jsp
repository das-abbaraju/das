<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" method="edit" var="employee_skill_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skills" var="employee_skills_list_url"/>
<div class="row">
    <div class="col-md-8">
        <tw:form formName="employee_skill_training" action="${employee_skill_url}" method="post"
                 class="form-horizontal js-validation" role="form">
            <fieldset>
                <div class="form-group">
                    <tw:label labelName="verified" class="col-md-3 control-label"><strong><s:text name="EMPLOYEE.SKILL.EDIT.FILE"/></strong></tw:label>
                    <div class="col-md-9">
                        <div class="checkbox">
                            <tw:label labelName="verified" class="control-label">
                                <s:if test="skillDocumentForm.verified">
                                    <tw:input inputName="verified" type="checkbox" value="true" checked="checked"/>
                                </s:if>
                                <s:else>
                                    <tw:input inputName="verified" type="checkbox" value="true"/>
                                </s:else>

                                <s:text name="EMPLOYEE.SKILL.USER_CERTIFICATION_MSG"/>
                            </tw:label>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-9 col-md-offset-3 form-actions">
                        <tw:button buttonName="update" type="submit" class="btn btn-primary"><s:text name="EMPLOYEE.SKILL.EDIT.UPDATE.BUTTON"/></tw:button>
                        <tw:button buttonName="cancel" type="button" class="btn btn-default cancel"><s:text name="EMPLOYEE.SKILL.EDIT.CANCEL.BUTTON"/></tw:button>
                    </div>
                </div>
            </fieldset>
        </tw:form>
    </div>
</div>