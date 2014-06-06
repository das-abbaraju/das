<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_alert_with_heading</s:param>
    <s:param name="header_title">${section_title}: Alert with Heading</s:param>

    <s:param name="description">
Alert with header on its own line. Used when the header is more like a sentence than an exclamation.
    </s:param>

    <s:param name="example_url">
        alerts/alert-message-with-heading/_alert-message-with-heading-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">alert-message-with-heading</s:param>

    <s:param name="html_code">
&lt;div class="alert alert-warning"&gt;
    &lt;h3&gt;Oh snap!&lt;/h3&gt;
    &lt;p&gt;
        Something is broken
    &lt;/p&gt;
&lt;/div&gt;
    </s:param>
</s:include>