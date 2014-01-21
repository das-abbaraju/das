<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_button_types</s:param>
    <s:param name="header_title">${section_title}: Button Types</s:param>

    <s:param name="description">
        <s:include value="button-type-descriptions.jsp" />
    </s:param>

    <s:param name="example_url">
        buttons/button-types/_button-types-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">button-types</s:param>

    <s:param name="html_code">
&lt;button type="button" class="btn btn-default"&gt;Default&lt;/button&gt;
&lt;button type="button" class="btn btn-primary"&gt;Primary&lt;/button&gt;
&lt;button type="button" class="btn btn-info"&gt;Info&lt;/button&gt;
&lt;button type="button" class="btn btn-success"&gt;Success&lt;/button&gt;
&lt;button type="button" class="btn btn-warning"&gt;Warning&lt;/button&gt;
&lt;button type="button" class="btn btn-danger"&gt;Danger&lt;/button&gt;
    </s:param>
</s:include>