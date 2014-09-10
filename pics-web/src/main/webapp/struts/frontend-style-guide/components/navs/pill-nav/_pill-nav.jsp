<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_pill_nav</s:param>
    <s:param name="header_title">${section_title}: Pill Nav</s:param>

    <s:param name="description">
Pill Navs are used to navigate within siloed sections of the site. Each item in the list loads a new page. Pill nav items have the option of displaying an icon.
    </s:param>

    <s:param name="example_url">
        navs/pill-nav/_pill-nav-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">pill-nav</s:param>

    <s:param name="html_code">
    &lt;ul class="nav nav-pills nav-stacked"&gt;
        &lt;li class="active"&gt;
            &lt;a href="#navs_nav_list"&gt;&lt;i class="icon-sitemap"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
        &lt;/li&gt;
        &lt;li&gt;
            &lt;a href="#navs_nav_list"&gt;&lt;i class="icon-certificate"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
        &lt;/li&gt;
        &lt;li class="nav-divider"&gt;&lt;/li&gt;
        &lt;li&gt;
            &lt;span class="nav-title"&gt;Some Title&lt;/span&gt;
        &lt;/li&gt;
        &lt;li&gt;
            &lt;a href="#navs_nav_list"&gt;&lt;i class="icon-group"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
        &lt;/li&gt;
        &lt;li&gt;
            &lt;a href="#navs_nav_list"&gt;&lt;i class="icon-user"&gt;&lt;/i&gt;Some List Item&lt;/a&gt;
        &lt;/li&gt;
    &lt;/ul&gt;
    </s:param>
</s:include>