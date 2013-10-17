<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee" method="edit" var="contractor_employee_edit_url">
    <s:param name="id">${employee.id}</s:param>
</s:url>

<tw:form formName="contractor_employee_edit" action="${contractor_employee_edit_url}" method="post" class="form-horizontal js-validation" autocomplete="off" role="form">
    <div class="form-group">
        <div class="col-md-9 col-md-offset-3 form-actions">
            <tw:button buttonName="save" type="submit" class="btn btn-success">Save</tw:button>
            <tw:button buttonName="cancel" type="button" class="btn btn-default cancel">Cancel</tw:button>
        </div>
    </div>
</tw:form>