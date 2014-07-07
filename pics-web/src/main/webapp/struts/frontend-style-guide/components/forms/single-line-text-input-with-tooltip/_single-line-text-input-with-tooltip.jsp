<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_form_control_with_tooltip</s:param>
    <s:param name="header_title">${section_title}: Text Input with Tooltip</s:param>

    <s:param name="description">
It is sometimes helpful to add a tooltip next to an input field to give users a bit more information about what is expected. Tooltips may also be used <a href="#forms_help_tooltip">outside of forms</a>.
    </s:param>

    <s:param name="example_url">
        forms/single-line-text-input-with-tooltip/_single-line-text-input-with-tooltip-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">single-line-text-input-with-tooltip</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;label name="someName1" class="col-md-3 control-label"&gt;&lt;strong&gt;Some Required Label&lt;/strong&gt;&lt;/label&gt;
    &lt;div class="col-md-4"&gt;
        &lt;input name="someName1" class="form-control" type="text" tabindex="1" value="Some Default Value"/&gt;
    &lt;/div&gt;
    &lt;div class="toolip-container col-md-1 col-xs-1"&gt;
           &lt;i
                class="icon-info-sign icon-large"
                title=""
                data-original-title="This is a sentence that explains the purpose of the form field."
                data-toggle="tooltip"
                data-placement="right"
                data-container="body"&gt;
            &lt;/i&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>