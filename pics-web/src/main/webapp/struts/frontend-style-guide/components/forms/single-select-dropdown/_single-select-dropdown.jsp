<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_single_select_dropdown</s:param>
    <s:param name="header_title">${section_title}: Dropdown</s:param>

    <s:param name="description">
Dropdowns are used when a list of options are available and only one may be selected. Use dropdowns over radio buttons (link to radio buttons) when there are more than two options available. Dropdowns may also be used outside of a form for view switching (e.g. changing the sort order of a list)
    </s:param>

    <s:param name="example_url">
        forms/single-select-dropdown/_single-select-dropdown-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">single-select-dropdown</s:param>

    <s:param name="html_code">
&lt;div class="form-group"&gt;
    &lt;label name="someName3" class="col-md-3 control-label"&gt;&lt;strong&gt;Some Required Label&lt;/strong&gt;&lt;/label&gt;
    &lt;div class="col-md-4 col-xs-11"&gt;
        &lt;select name="someName3" class="form-control select2Min" tabindex="3"&gt;
            &lt;option value="someValue" selected="selected"&gt;Some Default Option&lt;/option&gt;
            &lt;option value="someValue2"&gt;Some Other Option&lt;/option&gt;
        &lt;/select&gt;
    &lt;/div&gt;
&lt;/div&gt;
    </s:param>
</s:include>