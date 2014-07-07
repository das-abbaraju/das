<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Use indenting to show parent-child relationships in your markup.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">html-formatting-1</s:param>

    <s:param name="example_code">
BAD:
&lt;ul&gt;
&lt;li&gt;item 1&lt;/li&gt;
&lt;li&gt;item 2&lt;/li&gt;
&lt;/ul&gt;

&lt;ul&gt;&lt;li&gt;item 1&lt;/li&gt;&lt;li&gt;item2&lt;/li&gt;&lt;/ul&gt;

GOOD:
&lt;ul&gt;
    &lt;li&gt;item 1&lt;/li&gt;
    &lt;li&gt;item 2&lt;/li&gt;
&lt;/ul&gt;
    </s:param>
</s:include>