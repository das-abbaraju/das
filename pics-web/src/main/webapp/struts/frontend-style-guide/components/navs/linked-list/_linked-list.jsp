<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_linked_list</s:param>
    <s:param name="header_title">${section_title}: Linked List</s:param>

    <s:param name="description">
Convention of usage is to be determined.
    </s:param>

    <s:param name="example_url">
        navs/linked-list/_linked-list-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">linked-list</s:param>

    <s:param name="html_code">
&lt;div class="list-group"&gt;
    &lt;a href="#navs_linked_list" class="list-group-item"&gt;First&lt;/a&gt;
    &lt;a href="#navs_linked_list" class="list-group-item"&gt;Second&lt;/a&gt;
    &lt;a href="#navs_linked_list" class="list-group-item"&gt;Third&lt;/a&gt;
    &lt;a href="#navs_linked_list" class="list-group-item active"&gt;Fourth&lt;/a&gt;
    &lt;a href="#navs_linked_list" class="list-group-item"&gt;Fifth&lt;/a&gt;
&lt;/div&gt;
    </s:param>
</s:include>