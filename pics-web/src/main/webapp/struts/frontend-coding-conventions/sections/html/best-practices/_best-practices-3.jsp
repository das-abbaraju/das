<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid putting block-level elements inside of inline-level elements. 
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">html-best-practices-3</s:param>

    <s:param name="example_code">
BAD:
&lt;span&gt;&lt;div&gt;&lt;/div&gt;&lt;/span&gt;
    </s:param>
</s:include>