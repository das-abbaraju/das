<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_complex_form</s:param>
    <s:param name="header_title">${section_title}: Complex Form</s:param>

    <s:param name="description">
Here is an example of a complex form. Please refer to the following sections (under Forms) for details on each form component.
    </s:param>

    <s:param name="example_url">
        forms/complex-form/_complex-form-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">form-example</s:param>

    <s:param name="html_code">
&lt;form action="#" method="post" class="form-horizontal js-validation" role="form"&gt;
    &lt;fieldset&gt;
        &hellip;
    &lt;/fieldset&gt;
&lt;/form&gt;
    </s:param>
</s:include>