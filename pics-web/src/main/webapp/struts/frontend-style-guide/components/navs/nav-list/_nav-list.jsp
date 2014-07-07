<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_nav_list</s:param>
    <s:param name="header_title">${section_title}: Nav List</s:param>

    <s:param name="description">
Nav Lists are used to navigate within siloed sections of the site. Each item in the nav list loads a new page.
    </s:param>

    <s:param name="example_url">
        navs/nav-list/nav-list-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">nav-list</s:param>

    <s:param name="html_code">
&lt;ul class="nav nav-pills nav-stacked"&gt;
    &lt;li class="active"&gt;
        &lt;a href="#"&gt;&lt;i class="icon-sitemap"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
    &lt;/li&gt;
    &lt;li&gt;
        &lt;a href="#"&gt;&lt;i class="icon-certificate"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
    &lt;/li&gt;
    &lt;li&gt;
        &lt;a href="#"&gt;&lt;i class="icon-group"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
    &lt;/li&gt;
&lt;/ul&gt;
    </s:param>
</s:include>