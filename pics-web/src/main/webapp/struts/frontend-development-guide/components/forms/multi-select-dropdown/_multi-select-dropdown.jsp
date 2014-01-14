<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_multi_select_dropdown</s:param>
    <s:param name="header_title">${section_title}: Multiple Select</s:param>

    <s:param name="description">
Multiple selects are used when multiple options in a list may be selected. Use dropdowns over checkboxes (link to checkboxes) when there are more than two options available.
    </s:param>

    <s:param name="example_url">
        forms/multi-select-dropdown/_multi-select-dropdown-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">multi-select-dropdown</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;label name="groups" class="col-md-3 control-label"&gt;Some Label&lt;/label&gt;
    &lt;div class="col-md-4"&gt;
        &lt;select name="someName4" class="form-control select2" multiple tabindex="4"&gt;
            &lt;option value="someValue1" selected="selected"&gt;Some Default Option&lt;/option&gt;
            &lt;option value="someValue2"&gt;Some Other Option&lt;/option&gt;
            &lt;option value="someValue3"&gt;Some Other Option&lt;/option&gt;
        &lt;/select&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>

    <s:param name="struts_code">
    </s:param>
</s:include>