<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">Labels</s:param>
    <s:param name="section_id">labels</s:param>

    <s:param name="description">
The display state for a multi-select form field. Can also be used as a tag.
    </s:param>

    <s:param name="example_url">
        labels/_labels-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">labels</s:param>

    <s:param name="html_code">
&lt;span class="label label-default">New&lt;/span&gt;
&lt;span class="label label-primary"&gt;New&lt;/span&gt;
&lt;span class="label label-success"&gt;New&lt;/span&gt;
&lt;span class="label label-info"&gt;New&lt;/span&gt;
&lt;span class="label label-warning"&gt;New&lt;/span&gt;
&lt;span class="label label-danger"&gt;New&lt;/span&gt;
&lt;span class="label label-primary"&gt;New&lt;/span&gt;
    </s:param>
</s:include>