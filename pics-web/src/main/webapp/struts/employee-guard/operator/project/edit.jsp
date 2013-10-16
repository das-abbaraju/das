<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="operator_project_show_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="project" method="delete" var="operator_project_delete_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="project" method="edit" var="operator_project_edit_url">
    <s:param name="id">1</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Project</s:param>
    <s:param name="actions">
        <a href="${operator_project_delete_url}" class="btn btn-danger">Delete</a>
    </s:param>
</s:include>

<tw:form formName="operator_project_edit" action="${operator_project_edit_url}" method="post" class="form-horizontal">
    <fieldset>
        <div class="control-group">
            <tw:label labelName="name">Name*</tw:label>
            <div class="controls">
                <tw:input inputName="name" type="text" />
            </div>
        </div>
        
        <div class="control-group">
            <tw:label labelName="description">Description</tw:label>
            <div class="controls">
                <tw:textarea textareaName="description"></tw:textarea>
            </div>
        </div>
        
        <div class="control-group">
            <tw:label labelName="name">Location</tw:label>
            <div class="controls">
                <tw:input inputName="name" type="text" />
            </div>
        </div>
        
        <div class="control-group start-date">
            <tw:label labelName="start_year">Start Date</tw:label>
            <div class="controls">
                <tw:input inputName="start_year" type="text" placeholder="YYYY" class="input-mini" />
                <tw:input inputName="start_month" type="text" placeholder="MM" class="input-mini" />
                <tw:input inputName="start_day" type="text" placeholder="DD" class="input-mini" />
                
                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
            </div>
        </div>
        
        <div class="control-group end-date">
            <tw:label labelName="end_year">End Date</tw:label>
            <div class="controls">
                <tw:input inputName="end_year" type="text" placeholder="YYYY" class="input-mini" />
                <tw:input inputName="end_month" type="text" placeholder="MM" class="input-mini" />
                <tw:input inputName="end_day" type="text" placeholder="DD" class="input-mini" />
                
                <a href="#" class="btn btn-link date-picker" data-date-format="yyyy-mm-dd"><i class="icon-calendar"></i></a>
            </div>
        </div>
        
        <div class="control-group">
            <tw:label labelName="skills">Skills</tw:label>
            <div class="controls">
                <tw:select selectName="skills" multiple="true">
                    <option value="A">A</option>
                    <option value="B">B</option>
                    <option value="C">C</option>
                </tw:select>
            </div>
        </div>
        
        <div class="control-group">
            <tw:label labelName="roles">Roles</tw:label>
            <div class="controls">
                <tw:select selectName="roles" multiple="true">
                    <option value="A">A</option>
                    <option value="B">B</option>
                    <option value="C">C</option>
                </tw:select>
            </div>
        </div>
        
        <div class="control-group">
            <div class="controls">
                <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
                <a href="${operator_project_show_url}" class="btn btn-default">Cancel</a>
            </div>
        </div>
    </fieldset>
</tw:form>