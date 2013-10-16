<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" method="edit" var="employee_skill_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="skills" var="employee_skills_list_url" />

<tw:form formName="employee_manage_skill_training" action="${employee_skill_url}" method="post" class="form-horizontal" role="form">

    <fieldset>
        <div class="form-group">
            <tw:label labelName="proof" class="col-md-3 control-label"><strong>Proof</strong></tw:label>
            <div class="col-md-9">
                <div class="checkbox">
                    <tw:label labelName="proof" class="control-label">
                        <tw:input inputName="proof" type="checkbox" /> I certify that I have met all requirements.
                    </tw:label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="update" type="submit" class="btn btn-primary">Update</tw:button>
                <tw:button buttonName="cancel" type="button" class="btn btn-default cancel">Cancel</tw:button>
            </div>
        </div>
    </fieldset>


<%--     <fieldset>
        <h2>${skillDocumentForm.skillInfo.name}</h2>

        <p>
            ${skillDocumentForm.skillInfo.description}
        </p>

        <div class="form-group">
            <tw:label labelName="proof" class="col-md-3 control-label"><strong>Proof</strong></tw:label>
            <div class="col-md-9">
                <div class="checkbox">
                    <tw:label labelName="proof" class="control-label">
                        <tw:input inputName="proof" type="checkbox" /> I certify that I have met all requirements.
                    </tw:label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <tw:label labelName="complete_year" class="col-md-3 control-label"><strong>Completed</strong></tw:label>
            <div class="controls">
                <tw:input inputName="complete_year" type="text" class="input-mini" placeholder="YYYY" />
                <tw:input inputName="complete_month" type="text" class="input-mini" placeholder="MM" />
                <tw:input inputName="complete_day" type="text" class="input-mini" placeholder="DD" />

                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
            </div>
        </div>

        <div class="form-group">
            <div class="col-md-9 col-md-offset-3 form-actions">
                <tw:button buttonName="update" type="submit" class="btn btn-primary">Update</tw:button>
                <a href="${employee_skills_list_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset> --%>
</tw:form>