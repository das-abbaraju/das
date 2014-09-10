<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_toggle_switch</s:param>
    <s:param name="header_title">${section_title}: Toggle Switch</s:param>

    <s:param name="description">
    <div class="pull-right badge badge-info">AngularJS</div>
    Use a toggle switch to capture a simple Yes/No state.
    </s:param>

    <s:param name="example_url">
        forms/toggle-switch/_toggle-switch-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">toggle-switch</s:param>

    <s:param name="html_code">
&lt;toggle-switch ng-model="toggleModel" yes-label="Yes" no-label="No" class="pull-right"&gt;&lt;/toggle-switch&gt;
    </s:param>
</s:include>