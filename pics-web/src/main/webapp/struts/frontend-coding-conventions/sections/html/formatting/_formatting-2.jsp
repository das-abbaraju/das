<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Place double quotes around HTML attributes.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">html-formatting-2</s:param>

    <s:param name="example_code">
BAD:
&lt;div class='some-class'&gt;&lt;/div&gt;

GOOD:
&lt;div class="some-class"&gt;&lt;/div&gt;
    </s:param>
</s:include>