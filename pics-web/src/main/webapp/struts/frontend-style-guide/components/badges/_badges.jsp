<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-style-guide/components/_components-section.jsp">
    <s:param name="header_title">Badges</s:param>
    <s:param name="section_id">badges</s:param>

    <s:param name="description">
Badges are used to display counts. They are often embedded in headers or pills.
    </s:param>

    <s:param name="example_url">
        badges/_badges-example.jsp
    </s:param>

    <s:param name="accordian_parent_id">badges</s:param>

    <s:param name="html_code">
&lt;span class="badge badge-default">1&lt;/span&gt;
&lt;span class="badge badge-primary">2&lt;/span&gt;
&lt;span class="badge badge-info"&gt;3&lt;/span&gt;
&lt;span class="badge badge-success"&gt;4&lt;/span&gt;
&lt;span class="badge badge-warning"&gt;5&lt;/span&gt;
&lt;span class="badge badge-danger"&gt;6&lt;/span&gt;
&lt;a href="#badges"&gt;&lt;span class="badge badge-link"&gt;100&lt;/span&gt;&lt;/a&gt;
    </s:param>
</s:include>