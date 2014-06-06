<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_single_line_text_field</s:param>
    <s:param name="header_title">${section_title}: Text Input Field</s:param>

    <s:param name="description">
Text input fields are use for single lines of input.
    </s:param>

    <s:param name="example_url">
        forms/single-line-text-field/_single-line-text-field-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">single-line-text-field</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;label name="someName1" class="col-md-3 control-label"&gt;&lt;strong&gt;Some Required Label&lt;/strong&gt;&lt;/label&gt;
    &lt;div class="col-md-4"&gt;
        &lt;input name="someName1" class="form-control" type="text" tabindex="1" value="Some Default Value"&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>