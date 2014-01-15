<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_page_header</s:param>
    <s:param name="header_title">${section_title}: Page Header</s:param>

    <s:param name="description">
The page header is below the navigation bar and consists of a page title and optional description, breadcrumbs, and page action buttons.
    </s:param>

    <s:param name="example_url">
        page-layout/page-heading/_page-heading-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">page-heading</s:param>

    <s:param name="html_code">
&lt;div class="page-header pics"&gt;
    &lt;h1 class="title"&gt;Some Page Title&lt;/h1&gt;
    &lt;p class="subtitle"&gt;
        Some Page Subtitle
    &lt;/p&gt;
&lt;/div&gt;

&lt;ol class="breadcrumb"&gt;
    &lt;li&gt;&lt;a href="#"&gt;Root Page Title&lt;/a&gt;&lt;/li&gt;
    &lt;li&gt;&lt;a href="#"&gt;Previous Page Title&lt;/a&gt;&lt;/li&gt;
    &lt;li class="active"&gt;Current Page Title&lt;/li&gt;
&lt;/ol&gt;

&lt;div class="page-actions"&gt;
    &lt;button type="button" class="btn btn-primary"&gt;Some Action&lt;/button&gt;
    &lt;button type="button" class="btn btn-danger"&gt;Some Action&lt;/button&gt;
&lt;/div&gt;
    </s:param>

<%--     <s:param name="struts_code">
&lt;s:include value="/struts/employee-guard/_page-header.jsp"&gt;
    &lt;s:param name="title"&gt;Some Page Title&lt;/s:param&gt;
    &lt;s:param name="subtitle"&gt;Some Page Subtitle&lt;/s:param&gt;
    &lt;s:param name="actions"&gt;
        &lt;a href="#" class="btn btn-default"&gt;Some Action&lt;/a&gt;
    &lt;/s:param&gt;
    &lt;s:param name="breadcrumb_name"&gt;Some Breadcrumb Name&lt;/s:param&gt;
    &lt;s:param name="breadcrumb_id"&gt;Some Breadcrumb Id&lt;/s:param&gt;
&lt;/s:include&gt;
    </s:param> --%>
</s:include>