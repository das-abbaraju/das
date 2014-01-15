<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-development-guide/components/_components-section.jsp">
    <s:param name="header_title">Search</s:param>
    <s:param name="section_id">search</s:param>

    <s:param name="description">
Description unavailable
    </s:param>

    <s:param name="example_url">
        search-input/_search-input-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">search-input</s:param>

    <s:param name="html_code">
&lt;form action="#" class="search-query" role="form"&gt;
    &lt;fieldset&gt;
        &lt;div class="search-wrapper col-md-4"&gt;
            &lt;input name="searchTerm" type="text" class="form-control" placeholder="Search" value="${searchForm.searchTerm}"/&gt;
            &lt;i class="icon-search"&gt;&lt;/i&gt;
            &lt;ul class="search-results"&gt;&lt;/ul&gt;
        &lt;/div&gt;
    &lt;/fieldset&gt;
&lt;/form&gt;
    </s:param>
</s:include>