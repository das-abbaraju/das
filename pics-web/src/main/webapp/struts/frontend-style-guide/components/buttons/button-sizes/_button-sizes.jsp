<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">${section_title}: Button Sizes</s:param>
    <s:param name="section_id">${section_id_prefix}_button_sizes</s:param>

    <s:param name="description">
Medium buttons are most often used. Small buttons should be used if nesting inside another element—most likely a Section Header.
    </s:param>

    <s:param name="example_url">
        buttons/button-sizes/_button-sizes-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">button-sizes</s:param>

    <s:param name="html_code">
&lt;button type="button" class="btn btn-default btn-lg"&gt;Large&lt;/button&gt;
&lt;button type="button" class="btn btn-default"&gt;Medium&lt;/button&gt;
&lt;button type="button" class="btn btn-default btn-sm"&gt;Small&lt;/button&gt;
&lt;button type="button" class="btn btn-default btn-xs"&gt;X-small&lt;/button&gt;
    </s:param>
</s:include>