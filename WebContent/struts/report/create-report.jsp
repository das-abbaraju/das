<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="CreateReport" var="report_form" />
<s:url action="CreateReport" method="save" var="save_report" />

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Create Report</s:param>
</s:include>

<s:form cssClass="well form-horizontal" action="%{#save_report}" name="report_form" id="report_form">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="report_form_report_name">Report name:</label>
            <div class="controls">
            	<s:textfield name="report.name" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="report_form_report_modelType">Model:</label>
            <div class="controls">
            	<s:select list="@com.picsauditing.report.models.ModelType@values()" name="report.modelType"></s:select>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary" name="save">Create and Show Report</button>
        </div>
    </fieldset>
</s:form>
