<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_panels</s:param>
    <s:param name="header_title">${section_title}: Panels</s:param>

    <s:param name="description">
Description unavailable
    </s:param>

    <s:param name="example_url">
        information/panel/_panel-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">panels</s:param>

    <s:param name="html_code">
&lt;div class="panel panel-default"&gt;
    &lt;div class="panel-heading "&gt;
        &lt;h3 class="panel-title"&gt;Panel Title&lt;/h3&gt;
    &lt;/div&gt;
    &lt;div class="panel-body"&gt;
        Panel content
    &lt;/div&gt;
&lt;/div&gt;

&lt;div class="panel panel-primary"&gt;...&lt;/div&gt;
&lt;div class="panel panel-info"&gt;...&lt;/div&gt;
&lt;div class="panel panel-success"&gt;...&lt;/div&gt;
&lt;div class="panel panel-warning"&gt;...&lt;/div&gt;
&lt;div class="panel panel-danger"&gt;...&lt;/div&gt;
    </s:param>
</s:include>