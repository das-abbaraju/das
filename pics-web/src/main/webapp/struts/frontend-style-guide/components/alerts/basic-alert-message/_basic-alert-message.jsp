<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_basic_alert</s:param>
    <s:param name="header_title">${section_title}: Basic Alert</s:param>

    <s:param name="description">
Basic alerts have inline header and text. The dismiss button is not available.
    </s:param>

    <s:param name="example_url">
        alerts/basic-alert-message/_basic-alert-message-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">alert-messages</s:param>

    <s:param name="html_code">
&lt;div class="alert alert-primary"&gt;
    &lt;strong&gt;This is a test.&lt;/strong&gt; Nothing more.
&lt;/div&gt;
&lt;div class="alert alert-info"&gt;
    &lt;strong&gt;Hey listen!&lt;/strong&gt; Over here.
&lt;/div&gt;
&lt;div class="alert alert-success"&gt;
    &lt;strong&gt;All good.&lt;/strong&gt; Nothing to see here.
&lt;/div&gt;
&lt;div class="alert alert-warning"&gt;
    &lt;strong&gt;Warning!&lt;/strong&gt; Best check yo self, you're not looking too good.
&lt;/div&gt;
&lt;div class="alert alert-danger"&gt;
    &lt;strong&gt;Oh snap!&lt;/strong&gt; Something is broken.
&lt;/div&gt;
    </s:param>
</s:include>