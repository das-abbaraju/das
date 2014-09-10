<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_multi_line_text_field</s:param>
    <s:param name="header_title">${section_title}: Text Input Box</s:param>

    <s:param name="description">
Text input boxes are used for multiple lines of input. Add the class name "vertical-resize" to the textarea when you want to limit resizing to vertical only (e.g. when there is a tooltip).
    </s:param>

    <s:param name="example_url">
        forms/multi-line-text-field/_multi-line-text-field-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">multi-line-text-field</s:param>

    <s:param name="html_code">
&lt;div class="col-md-4"&gt;
    &lt;textarea name="someName2" class="form-control vertical-resize" tabindex="2"&gt;Some Default Value&lt;/textarea&gt;
&lt;/div&gt;
&lt;div class="toolip-container col-md-1 col-xs-1"&gt;
   &lt;i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="right" title="" data-original-title="This is a sentence that explains the purpose of the form field." data-container="body"&gt;&lt;/i&gt;
&lt;/div&gt;
    </s:param>
</s:include>