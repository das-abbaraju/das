<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_radio_button</s:param>
    <s:param name="header_title">${section_title}: Radio Button</s:param>

    <s:param name="description">
Use radio buttons to capture a selection when up to three options are available.  For more than three options, use the <a href="#forms_multi_select_dropdown">Multiple Select</a> component.
    </s:param>

    <s:param name="example_url">
        forms/radio-button/_radio-button-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">radio-button</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;label&gt;&lt;input type="radio" name="somefield"/&gt; Some Value 1&lt;/label&gt;&lt;br&gt;
    &lt;label&gt;&lt;input type="radio" name="somefield"/&gt; Some Value 2&lt;/label&gt;&lt;br&gt;
    &lt;label&gt;&lt;input type="radio" name="somefield"/&gt; Some Value 3&lt;/label&gt;
&lt;/div&gt;
    </s:param>
</s:include>