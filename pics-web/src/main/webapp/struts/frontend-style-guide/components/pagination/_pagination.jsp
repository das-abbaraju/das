<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">Pagination</s:param>
    <s:param name="section_id">pagination</s:param>

    <s:param name="description">
Pagination is used to easily navigation between multiple pages of information.
    </s:param>

    <s:param name="example_url">
        pagination/_pagination-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">pagination</s:param>

    <s:param name="html_code">
&lt;ul class="pagination">
    &lt;li&gt;&lt;a href="#pagination"&gt;&lt;i class="icon-angle-left"&gt;&lt;/i&gt;&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#pagination"&gt;1&lt;/a&gt;&lt;/li&gt;
    &lt;li class="active"&gt;&lt;a href="#pagination"&gt;2&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#pagination"&gt;3&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#pagination"&gt;4&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#pagination"&gt;5&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#pagination"&gt;&lt;i class="icon-angle-right"&gt;&lt;/i&gt;&lt;/a&gt;&lt;/li&gt;
&lt;/ul>&gt;
    </s:param>
</s:include>