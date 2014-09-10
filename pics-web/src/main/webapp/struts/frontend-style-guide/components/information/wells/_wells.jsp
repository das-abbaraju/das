<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="section_id">${section_id_prefix}_wells</s:param>
    <s:param name="header_title">${section_title}: Wells</s:param>

    <s:param name="description">
Wells are used to display secondary information.
    </s:param>

    <s:param name="example_url">
        information/wells/_wells-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">wells</s:param>

    <s:param name="html_code">
&lt;div class="well"&gt;Look, I'm in a well!&lt;/div&gt;
    </s:param>
</s:include>