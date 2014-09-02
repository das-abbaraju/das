<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_complex_form</s:param>
    <s:param name="header_title">${section_title}: Complex Form</s:param>

    <s:param name="description">
Below is an example of a complex form. Please refer to the following sections (under Forms) for details on each component.
    </s:param>

    <s:param name="example_url">
        forms/complex-form/_complex-form-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">form-example</s:param>

    <s:param name="html_code">
&lt;form action="#" method="post" class="form-horizontal js-validation" role="form"&gt;
    &lt;fieldset&gt;
        &lt;div class="form-group"&gt;
            &lt;label name="someName1" class="col-md-3 control-label"&gt;
                &lt;strong&gt;Some Required Label&lt;/strong&gt;
            &lt;/label&gt;
            &lt;div class="col-md-4"&gt;
                &lt;input name="someName1" class="form-control" type="text" tabindex="1" value="Some Default Value"/&gt;
            &lt;/div&gt;
            &lt;div class="toolip-container col-md-1 col-xs-1"&gt;
               &lt;i class="icon-info-sign icon-large" data-toggle="tooltip" data-placement="top" title=""
               data-original-title="This is a sentence that explains the purpose of the form field."
               data-container="body"&gt;&lt;/i&gt;
            &lt;/div&gt;
        &lt;/div&gt;
        &hellip;
    &lt;/fieldset&gt;
&lt;/form&gt;
    </s:param>
</s:include>