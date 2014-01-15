<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_section_header</s:param>
    <s:param name="header_title">${section_title}: Section Heading</s:param>

    <s:param name="description">
Section headers help organize related content into sections. When these sections contain content that may be edited, an edit icon is floated to the right. Clicking the icon toggles the section between view and edit states.
    </s:param>

    <s:param name="example_url">
        page-layout/section-heading/_section-heading-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">section-heading</s:param>

    <s:param name="html_code">
&lt;section class="employee-guard-section"&gt;
    &lt;h1&gt;
        &lt;div class="row"&gt;
            &lt;div class="col-md-9 col-xs-9"&gt;
                &lt;i class="icon-picture icon-large"&gt;&lt;/i&gt;
                Some Section Heading
            &lt;/div&gt;
        &lt;/div&gt;
    &lt;/h1&gt;
&lt;/section&gt;
    </s:param>
</s:include>