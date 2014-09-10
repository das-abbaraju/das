<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">Labels</s:param>
    <s:param name="section_id">labels</s:param>

    <s:param name="description">
Labels are used as tags or when displaying a multi-select option in read-only mode.
    </s:param>

    <s:param name="example_url">
        labels/_labels-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">labels</s:param>

    <s:param name="html_code">
&lt;span class="label label-default">Default&lt;/span&gt;
&lt;span class="label label-primary">Primary&lt;/span&gt;
&lt;span class="label label-info"&gt;Info&lt;/span&gt;
&lt;span class="label label-success"&gt;Success&lt;/span&gt;
&lt;span class="label label-warning"&gt;Warning&lt;/span&gt;
&lt;span class="label label-danger"&gt;Danger&lt;/span&gt;
&lt;a href="#labels"&gt;&lt;span class="label label-link"&gt;Link&lt;/span&gt;&lt;/a&gt;
    </s:param>
</s:include>