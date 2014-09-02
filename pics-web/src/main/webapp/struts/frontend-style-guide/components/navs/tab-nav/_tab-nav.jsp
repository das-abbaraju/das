<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_tab_nav</s:param>
    <s:param name="header_title">${section_title}: Tab Nav</s:param>

    <s:param name="description">
Tab Navs add quick, dynamic tab functionality to transition through panes of local content.
    </s:param>

    <s:param name="example_url">
        navs/tab-nav/tab-nav-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">tab-nav</s:param>

    <s:param name="html_code">
&lt;div class="col-md-10"&gt;

    &lt;ul class="nav nav-tabs" role="tablist"&gt;
        &lt;li class="active"&gt;
            &lt;a href="#current-tab" role="tab" data-toggle="tab"&gt;
            Current Tab
            &lt;/a&gt;
        &lt;/li&gt;
        &lt;li&gt;
            &lt;a href="#second-tab" role="tab" data-toggle="tab"&gt;
            Second Tab
            &lt;/a&gt;
        &lt;/li&gt;
        &lt;li&gt;
            &lt;a href="#third-tab" role="tab" data-toggle="tab"&gt;
            Third Tab
            &lt;/a&gt;
        &lt;/li&gt;
    &lt;/ul&gt;
&lt;/div&gt;

    </s:param>
</s:include>