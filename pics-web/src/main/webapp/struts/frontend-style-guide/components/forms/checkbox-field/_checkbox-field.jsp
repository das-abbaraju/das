<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_checkbox_field</s:param>
    <s:param name="header_title">${section_title}: Checkbox</s:param>

    <s:param name="description">
Use checkboxes to capture a simple binary state. Multiple may be selected.
    </s:param>

    <s:param name="example_url">
        forms/checkbox-field/_checkbox-field-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">checkbox-field</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;fieldset&gt;
        &lt;input type="checkbox"/&gt; Some Value
    &lt;/fieldset&gt;
&lt;/div&gt;
    </s:param>
</s:include>